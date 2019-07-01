package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.field.Field;
import quan.database.log.DataLog;
import quan.database.log.FieldLog;
import quan.database.log.RootLog;
import quan.database.log.VersionLog;

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

    /**
     * 执行总次数(事务本身为单位)
     */
    private static AtomicLong totalCount = new AtomicLong();

    /**
     * 事务本身执行时间阈值(ms)，大于等于此值为慢事务
     */
    private static int slowTimeThreshold;

    /**
     * 慢事务执行次数(事务本身为单位)
     */
    private static AtomicLong slowCount = new AtomicLong();

    /**
     * 事务中的逻辑执行冲突次数阈值
     */
    private static int conflictThreshold;

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
    private TreeSet<Lock> notCachedRowLocks = new TreeSet<>();

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
    private List<AfterTask> commitAfterTasks = new ArrayList<>();

    /**
     * 事务回滚之后需要执行的任务
     */
    private List<AfterTask> rollbackAfterTasks = new ArrayList<>();


    public long getId() {
        return id;
    }

    public boolean isFailed() {
        return failed;
    }

    public long getTaskStartTime() {
        return taskStartTime;
    }

    public static void setSlowTimeThreshold(int slowTimeThreshold) {
        Transaction.slowTimeThreshold = slowTimeThreshold;
    }

    public static void setConflictThreshold(int conflictThreshold) {
        Transaction.conflictThreshold = conflictThreshold;
    }

    public void addVersionLog(Data data) {
        if (data.isExpired()) {
            throw new IllegalStateException("数据已过期");
        }
        if (versionLogs.containsKey(data)) {
            return;
        }
        VersionLog versionLog = new VersionLog(data);
        versionLogs.put(versionLog.getData(), versionLog);
    }


    public void addFieldLog(FieldLog fieldLog) {
        fieldLogs.put(fieldLog.getField(), fieldLog);
    }

    public FieldLog getFieldLog(Field field) {
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
     * 当前事务
     *
     * @return
     */
    public static Transaction current() {
        return threadLocal.get();
    }

    public static Transaction get() {
        Transaction current = threadLocal.get();
        if (current == null) {
            throw new IllegalStateException("当前不在事务中");
        }
        return current;
    }

    /**
     * 开始事务
     */
    private static Transaction begin() {
        Transaction current = current();
        if (current == null) {
            current = new Transaction();
            threadLocal.set(current);
        } else {
            throw new IllegalStateException("当前已经在事务中了");
        }
        return current;
    }

    /**
     * 结束当前事务
     */
    private static void end(boolean failed) {
        Transaction current = get();
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
        Transaction current = get();

        long _totalCount = totalCount.incrementAndGet();
        if (_totalCount <= 0) {
            totalCount.set(1);
            slowCount.set(0);
            conflictCount.set(0);
        }

        long costTime = System.currentTimeMillis() - current.startTime;

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

        if (slow || conflict) {
            logger.debug("事务{}结束,执行成功:{},耗时:{}ms,逻辑冲突次数:{} | 事务执行总次数:{},慢事务次数:{},频繁冲突事务次数:{}",
                    current.id, !current.failed, costTime, current.taskConflictCount, totalCount, slowCount, conflictCount);
        }

    }

    /**
     * 在事务中执行任务
     *
     * @param task
     */
    public static void execute(Task task) {
        Transaction current = current();
        if (current != null) {
            execute0(task);
        } else {
            execute1(task);
        }
    }

    /**
     * 当前已经在事务之中了，直接执行
     *
     * @param task
     */
    public static void execute0(Task task) {
        Transaction transaction = get();
        boolean result = task.run();
        if (!result) {
            transaction.failed = true;
        }
    }


    /**
     * 当前已经不在事务之中，开启新事务再执行
     *
     * @param task
     */
    public static void execute1(Task task) {
        Transaction transaction = Transaction.current();
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
            throw e;
        } finally {
            end(failed);
        }

    }


    /**
     * 排序加锁，保证不同线程按照同一顺序竞争锁，防止死锁
     */
    private void lock() {
        TreeSet<Cache> caches = new TreeSet<>();
        for (DataLog dataLog : dataLogs.values()) {
            caches.add(dataLog.getCache());
            cachedRowLocks.add(LockPool.getLock(dataLog.getCache(), dataLog.getKey().getK()));

        }
        for (Cache cache : caches) {
            tableLocks.add(cache.getLock());
        }

        TreeSet<Integer> rowLockIndexes = new TreeSet<>();
        for (Data data : versionLogs.keySet()) {
            Cache cache = data.getCache();
            if (cache != null) {
                rowLockIndexes.add(LockPool.getLockIndex(cache, data.getKey()));
            } else {
                //没有注册缓存
                notCachedRowLocks.add(data.getLock());
            }
        }

        for (Integer rowLockIndex : rowLockIndexes) {
            cachedRowLocks.add(LockPool.getLock(rowLockIndex));
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
            failed = false;
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

    private static void runAfterTasks(List<AfterTask> afterTasks) {
        for (AfterTask afterTask : afterTasks) {
            try {
                afterTask.run();
            } catch (Exception e) {
                afterTask.onException(e);
            }
        }
        afterTasks.clear();
    }

    /**
     * 在事务提交或者回滚之后执行任务
     *
     * @param task
     * @param committed true:提交执行,false:滚之后执行
     */
    public static void execute(AfterTask task, boolean committed) {
        Transaction transaction = Transaction.get();
        if (committed) {
            transaction.commitAfterTasks.add(task);
        } else {
            transaction.rollbackAfterTasks.add(task);
        }
    }

}
