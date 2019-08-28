package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class Transaction {

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    private static final AtomicLong nextId = new AtomicLong();

    private static long lastPrintCountTime = System.currentTimeMillis();

    /**
     * 打印统计信息间隔(秒)
     */
    static int printCountInterval = 3600;

    /**
     * 执行总次数(事务本身为单位)
     */
    private static AtomicLong totalCount = new AtomicLong();

    /**
     * 事务本身执行时间阈值(ms)，大于等于此值为慢事务
     */
    static int slowTimeThreshold;

    /**
     * 慢事务执行次数(事务本身为单位)
     */
    private static AtomicLong slowCount = new AtomicLong();

    /**
     * 事务中的逻辑执行冲突次数阈值
     */
    static int conflictThreshold;

    /**
     * 事务逻辑冲突次数大于等于[conflictThreshold]的事务执行次数(以事务为单位)
     */
    private static AtomicLong conflictCount = new AtomicLong();

    /**
     * 事务本身的开始时间
     */
    private long startTime = System.currentTimeMillis();

    /**
     * 当前事务逻辑的开始时间，如果事务冲突重试，需要重新计时
     */
    private long taskStartTime;

    /**
     * 当前事务的逻辑冲突次数
     */
    private int taskConflictCount;

    /**
     * 事务ID
     */
    private long id = nextId.incrementAndGet();

    /**
     * 事务是否失败
     */
    private boolean failed;

    /**
     * 表级锁
     */
    private List<ReadWriteLock> tableLocks = new ArrayList<>();

    /**
     * 行级锁，数据受缓存管理时
     */
    private List<Lock> cachedRowLocks = new ArrayList<>();

    /**
     * 行级锁，数据不受缓存管理时
     */
    private List<Lock> notCachedRowLocks = new ArrayList<>();

    /**
     * 记录Data的版本号
     */
    private Map<Data, VersionLog> versionLogs = new HashMap<>();

    /**
     * 记录节点(Bean和集合)的根对象
     */
    private Map<Node, RootLog> rootLogs = new HashMap<>();

    /**
     * 记录字段值
     */
    private Map<Field, FieldLog> fieldLogs = new HashMap<>();

    /**
     * 记录Data的缓存和创建删除
     */
    private Map<DataLog.Key, DataLog> dataLogs = new HashMap<>();

    /**
     * 事务提交之后需要执行的任务
     */
    private List<Runnable> commitAfterTasks = new ArrayList<>();

    /**
     * 事务回滚之后需要执行的任务
     */
    private List<Runnable> rollbackAfterTasks = new ArrayList<>();


    public long getId() {
        return id;
    }

    public boolean isFailed() {
        return failed;
    }

    long getTaskStartTime() {
        return taskStartTime;
    }


    void addVersionLog(Data data) {
        if (data == null) {
            return;
        }
        if (data.getCache() != null) {
            data.getCache().checkWorkable();
        }
        if (data._isExpired()) {
            throw new IllegalStateException("数据已过期");
        }
        data.touch();
        if (versionLogs.containsKey(data)) {
            return;
        }
        VersionLog versionLog = new VersionLog(data);
        versionLogs.put(versionLog.getData(), versionLog);
    }


    void addFieldLog(FieldLog fieldLog) {
        fieldLogs.put(fieldLog.getField(), fieldLog);
    }

    FieldLog getFieldLog(Field field) {
        return fieldLogs.get(field);
    }

    void addRootLog(RootLog rootLog) {
        rootLogs.put(rootLog.getNode(), rootLog);
    }

    RootLog getRootLog(Node node) {
        return rootLogs.get(node);
    }

    DataLog getDataLog(Object key) {
        return dataLogs.get(key);
    }

    void addDataLog(DataLog dataLog) {
        dataLogs.put(dataLog.getKey(), dataLog);
    }

    /**
     * 当前是不是处于事务之中
     *
     * @return
     */
    public static boolean isInside() {
        return threadLocal.get() != null;
    }


    /**
     * 获取当前事务
     *
     * @param check
     * @return
     */
    public static Transaction get(boolean check) {
        Transaction transaction = threadLocal.get();
        if (check && transaction == null) {
            throw new IllegalStateException("当前不在事务中");
        }
        return transaction;
    }

    /**
     * 获取当前事务
     *
     * @return
     */
    public static Transaction get() {
        return get(false);
    }

    /**
     * 开始事务
     */
    private static Transaction begin() {
        Transaction transaction = get();
        if (transaction == null) {
            transaction = new Transaction();
            threadLocal.set(transaction);
        } else {
            throw new IllegalStateException("当前已经在事务中了");
        }
        return transaction;
    }

    /**
     * 结束当前事务
     */
    private static void end(boolean failed) {
        Transaction current = get(true);
        try {
            current.failed = current.failed || failed;
            if (current.isFailed()) {
                current.rollback();
            } else {
                current.commit();
            }
            count();
        } finally {
            threadLocal.set(null);
        }
    }

    /**
     * 统计事务执行次数，慢事务次数，冲突次数
     */
    private static void count() {
        Transaction current = get(true);

        long _totalCount = totalCount.incrementAndGet();
        if (_totalCount <= 0) {
            totalCount.set(1);
            slowCount.set(0);
            conflictCount.set(0);
        }

        long currentTime = System.currentTimeMillis();
        long costTime = currentTime - current.startTime;

        boolean slow = false;
        if (slowTimeThreshold > 0 && costTime >= slowTimeThreshold) {
            slowCount.incrementAndGet();
            slow = true;
        }

        boolean conflict = false;
        if (conflictThreshold > 0 && current.taskConflictCount > conflictThreshold) {
            conflictCount.incrementAndGet();
            conflict = true;
        }

        if (currentTime - lastPrintCountTime >= printCountInterval * 1000) {
            lastPrintCountTime = currentTime;
            logger.info("事务执行总次数:{},慢事务次数:{},频繁冲突事务次数:{}", totalCount, slowCount, conflictCount);
        }

        if (slow || conflict) {
//            logger.debug("事务{}结束,执行成功:{},耗时:{}ms,逻辑冲突次数:{} | 事务执行总次数:{},慢事务次数:{},频繁冲突事务次数:{}",
//                    current.id, !current.failed, costTime, current.taskConflictCount, totalCount, slowCount, conflictCount);
        }

    }

    public static void breakdown() {
        Transaction.get(true).failed = true;
        throw new BreakdownException();
    }

    /**
     * 在事务中执行任务，执行线程为调用方法的当前线程
     *
     * @param task
     */
    public static void execute(Task task) {
        if (isInside()) {
            insideExecute(task);
        } else {
            outsideExecute(task);
        }
    }

    /**
     * 在事务内部执行
     *
     * @param task
     */
    public static void insideExecute(Task task) {
        Transaction transaction = get(true);
        if (!task.run()) {
            transaction.failed = true;
        }
    }


    /**
     * 在事务外部执行，需要开启新事务
     *
     * @param task
     */
    public static void outsideExecute(Task task) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            throw new IllegalStateException("当前已经在事务中了");
        }

        transaction = Transaction.begin();
        boolean failed = false;

        try {
            while (true) {
                transaction.taskStartTime = System.currentTimeMillis();

                boolean result = task.run();
                if (!result) {
                    failed = true;
                    return;
                }

                transaction.lock();
                if (transaction.isConflict()) {
                    //有冲突，清空日志，重新执行
                    transaction.taskConflictCount++;
                    transaction.clearLogs();
                } else {
                    return;
                }
            }
        } catch (Throwable e) {
            failed = true;
            if (!(e instanceof BreakdownException)) {
                throw e;
            }
        } finally {
            end(failed);
        }

    }


    /**
     * 排序加锁，保证不同线程按照同一顺序竞争锁，防止死锁
     */
    private void lock() {
        tableLocks.clear();
        cachedRowLocks.clear();
        notCachedRowLocks.clear();

        TreeSet<Cache> caches = new TreeSet<>();
        for (DataLog dataLog : dataLogs.values()) {
            Cache cache = dataLog.getCache();
            cache.checkWorkable();
            caches.add(cache);
            cachedRowLocks.add(LockPool.getLock(cache, dataLog.getKey().getK()));

        }
        for (Cache cache : caches) {
            tableLocks.add(cache.getLock());
        }

        TreeSet<Integer> rowLockIndexes = new TreeSet<>();
        TreeSet<Data> notCachedRowLocksIndexes = new TreeSet<>();
        for (Data data : versionLogs.keySet()) {
            Cache cache = data.getCache();
            if (cache != null) {
                cache.checkWorkable();
                rowLockIndexes.add(LockPool.getLockIndex(cache, data.getKey()));
            } else {
                //没有注册缓存
                notCachedRowLocksIndexes.add(data);
            }
        }

        for (Integer rowLockIndex : rowLockIndexes) {
            cachedRowLocks.add(LockPool.getLock(rowLockIndex));
        }

        for (Data data : notCachedRowLocksIndexes) {
            notCachedRowLocks.add(data._getLock());
        }

        //存档时要阻塞
        for (ReadWriteLock tableLock : tableLocks) {
            tableLock.readLock().lock();
        }

        for (Lock rowLock : cachedRowLocks) {
            rowLock.lock();
        }

        for (Lock rowLock : notCachedRowLocks) {
            rowLock.lock();
        }
    }

    private void unlock() {
        for (ReadWriteLock tableLock : tableLocks) {
            tableLock.readLock().unlock();
        }
        tableLocks.clear();

        for (Lock rowLock : cachedRowLocks) {
            rowLock.unlock();
        }
        cachedRowLocks.clear();

        for (Lock rowLock : notCachedRowLocks) {
            rowLock.unlock();
        }
        notCachedRowLocks.clear();
    }


    private boolean isConflict() {
        for (DataLog dataLog : dataLogs.values()) {
            if (dataLog.isConflict()) {
                return true;
            }
        }

        for (VersionLog versionLog : versionLogs.values()) {
            if (versionLog.isConflict()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 清空当前事务日志，事务执行结束或者事务逻辑发生冲突时需要
     */
    private void clearLogs() {
        try {
            dataLogs.clear();
            versionLogs.clear();
            fieldLogs.clear();
            rootLogs.clear();
        } finally {
            unlock();
        }
    }

    /**
     * 事务提交
     */
    private void commit() {
        for (DataLog dataLog : dataLogs.values()) {
            dataLog.commit();
        }
        for (VersionLog versionLog : versionLogs.values()) {
            versionLog.commit();
        }
        for (RootLog rootLog : rootLogs.values()) {
            rootLog.commit();
        }
        for (FieldLog fieldLog : fieldLogs.values()) {
            fieldLog.commit();
        }

        clearLogs();

        runAfterTasks(commitAfterTasks);
    }

    /**
     * 事务回滚
     */
    private void rollback() {
        clearLogs();
        runAfterTasks(rollbackAfterTasks);
    }

    private static void runAfterTasks(List<Runnable> afterTasks) {
        for (Runnable afterTask : afterTasks) {
            try {
                afterTask.run();
            } catch (Exception e1) {
                if (afterTask instanceof AfterTask) {
                    try {
                        ((AfterTask) afterTask).onException(e1);
                    } catch (Exception e2) {
                        logger.error("", e2);
                    }
                } else {
                    logger.error("", e1);
                }
            }
        }
        afterTasks.clear();
    }

    /**
     * @param task
     * @param committed true:提交执行,false:滚之后执行
     */
    public static void addAfterTask(Runnable task, boolean committed) {
        addAfterTasks(Arrays.asList(task), committed);
    }

    public static void addAfterTasks(List<Runnable> tasks, boolean committed) {
        Transaction transaction = Transaction.get(true);
        if (committed) {
            transaction.commitAfterTasks.addAll(tasks);
        } else {
            transaction.rollbackAfterTasks.addAll(tasks);
        }
    }

    /**
     * 打断事务异常，用于标记事务失败，不会往外抛出
     */
    static class BreakdownException extends RuntimeException {
    }


}
