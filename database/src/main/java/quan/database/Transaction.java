package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.exception.TransactionException;
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
     * 事务中的逻辑执行冲突次数阈值,大于等于此值
     */
    private static int conflictNumThreshold;

    /**
     * 频繁冲突的事务执行次数(事务本身为单位)
     */
    private static AtomicLong frequentCount = new AtomicLong();

    /**
     * 事务本身的开始时间
     */
    private long startTime = System.currentTimeMillis();

    /**
     * 事务逻辑的开始时间，如果事务冲突回滚，需要重新计时
     */
    private long taskStartTime;

    /**
     * 当前事务的逻辑冲突次数
     */
    private int conflictCount;

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
    private List<Lock> rowLocks = new ArrayList<>();

    /**
     * 行级锁，数据不受缓存管理时
     */
    private TreeSet<Lock> rowLocks2 = new TreeSet<>();

    /**
     * 记录Data的版本号
     */
    private Map<Data, VersionLog> versionLogs = new HashMap<>();

    /**
     * 记录Bean的根对象
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

    public static void setConflictNumThreshold(int conflictNumThreshold) {
        Transaction.conflictNumThreshold = conflictNumThreshold;
    }

    /**
     * 标记当前事务为失败状态
     */
    public static void fail() {
        Transaction current = get();
        if (current != null) {
            current.failed = true;
        }
    }

    public static void breakdown() {
        Transaction current = get();
        if (current != null) {
            current.failed = true;
            throw new TransactionException("事务被打断");
        }
    }

    public void addVersionLog(Data data) {
        if (versionLogs.containsKey(data)) {
            return;
        }
        VersionLog versionLog = new VersionLog(data);
        versionLogs.put(versionLog.getData(), versionLog);
    }

    public VersionLog getVersionLog(Data data) {
        return versionLogs.get(data);
    }

    public void addFieldLog(FieldLog fieldLog) {
        fieldLogs.put(fieldLog.getField(), fieldLog);
    }

    public FieldLog getFieldLog(Field field) {
        return fieldLogs.get(field);
    }

    public void addRootLog(RootLog rootLog) {
        rootLogs.put(rootLog.getNode(), rootLog);
    }

    public RootLog getRootLog(Node node) {
        return rootLogs.get(node);
    }

    public DataLog getDataLog(Object key) {
        return dataLogs.get(key);
    }

    public void addDataLog(DataLog dataLog) {
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
            throw new UnsupportedOperationException("当前不在事务中");
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
            throw new UnsupportedOperationException("当前已经在事务中了");
        }
        return current;
    }

    /**
     * 结束当前事务
     *
     * @param fail 事务是否失败
     */
    private static void end(boolean fail) {
        Transaction current = get();

        current.failed = current.failed || fail;

        if (current.isFailed()) {
            current.rollback(true);
        } else {
            current.commit();
        }

        threadLocal.set(null);

        totalCount.incrementAndGet();
        long costTime = System.currentTimeMillis() - current.startTime;

        boolean slow = false;
        if (slowTimeThreshold > 0 && costTime >= slowTimeThreshold) {
            slowCount.incrementAndGet();
            slow = true;
        }
        boolean conflict = false;
        if (conflictNumThreshold > 0 && current.conflictCount > conflictNumThreshold) {
            frequentCount.incrementAndGet();
            conflict = true;
        }
        if (slow || conflict) {
            logger.debug("事务{}结束,执行成功:{},耗时:{}ms,逻辑冲突次数:{} | 事务执行总次数:{},慢事务次数:{},频繁冲突事务次数:{}",
                    current.id, !current.failed, costTime, current.conflictCount, totalCount, slowCount, frequentCount);
        }

    }

    /**
     * 在事务中执行任务
     *
     * @param task
     */
    public static void execute(Runnable task) {
        if (current() != null) {
            //当前已经在事务中了，直接执行任务
            task.run();
            return;
        }

        //开启事务再执行任务
        boolean fail = false;
        Transaction current = begin();
        int count = 0;
        try {
            while (true) {
                count++;
                if (current.conflictCount > 2) {
//                    logger.debug("当前第{}次执行事务{}", count, current.getId());
                }

                current.taskStartTime = System.currentTimeMillis();

                task.run();
                current.lock();

                if (current.isConflict()) {
                    //有冲突，其他事务也修改了数据
                    current.conflictCount++;
                    current.rollback(false);
                } else {
                    return;
                }
            }
        } catch (Throwable e) {
            fail = true;
            throw e;
        } finally {
            end(fail);
        }
    }


    /**
     * 排序加锁，保证不同线程按照同一顺序竞争锁，防止死锁
     */
    private void lock() {
        TreeSet<Cache> caches = new TreeSet<>();
        for (DataLog dataLog : dataLogs.values()) {
            caches.add(dataLog.getCache());
            rowLocks.add(LockPool.getLock(dataLog.getCache(), dataLog.getKey().getK()));

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
                rowLocks2.add(data.getLock());
            }
        }
        for (Integer rowLockIndex : rowLockIndexes) {
            rowLocks.add(LockPool.getLock(rowLockIndex));
        }

        //存档时要阻塞
        for (ReadWriteLock tableLock : tableLocks) {
            tableLock.readLock().lock();
        }

        for (Lock rowLock : rowLocks) {
            rowLock.lock();
        }

        for (Lock lock : rowLocks2) {
            lock.lock();
        }
    }

    private void unlock() {
        for (ReadWriteLock tableLock : tableLocks) {
            tableLock.readLock().unlock();
        }
        tableLocks.clear();

        for (Lock rowLock : rowLocks) {
            rowLock.unlock();
        }
        rowLocks.clear();

        for (Lock lock : rowLocks2) {
            lock.unlock();
        }
        rowLocks2.clear();
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


    private void commit() {
        try {
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
     * 执行失败或者和其他事务冲突时回滚事务
     *
     * @param end 是不是结束事务的回滚
     */
    private void rollback(boolean end) {
        try {
            dataLogs.clear();
            versionLogs.clear();
            fieldLogs.clear();
            rootLogs.clear();
            failed = false;
        } finally {
            unlock();
        }

//        logger.debug("回滚事务{}", getId());
    }

}
