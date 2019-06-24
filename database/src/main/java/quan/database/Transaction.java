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

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class Transaction {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    private static final AtomicLong idGenerator = new AtomicLong();

    /**
     * 事务ID
     */
    private long id = idGenerator.incrementAndGet();


    /**
     * 事务是否失败
     */
    private boolean failed;

    private List<Lock> locks = new ArrayList<>();

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
     * 记录Data的创建删除和版本号
     */
    private Map<DataLog.Key, DataLog> dataLogs = new HashMap<>();

    public long getId() {
        return id;
    }

    public boolean isFailed() {
        return failed;
    }

    /**
     * 标记当前事务为失败状态
     */
    public static void fail() {
        Transaction current = current();
        if (current != null) {
            current.failed = true;
        }
    }

    public static void breakdown() {
        Transaction current = current();
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
        if (dataLogs.containsKey(dataLog.getKey())) {
            return;
        }
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

    /**
     * 开始事务
     */
    private static Transaction begin() {
        Transaction current = current();
        if (current == null) {
            current = new Transaction();
            threadLocal.set(current);
        } else {
            throw new RuntimeException("当前已经在事务中了");
        }
        return current;
    }

    /**
     * 结束当前事务
     *
     * @param fail 事务是否失败
     */
    private static void end(boolean fail) {
        Transaction current = current();
        if (current == null) {
            throw new RuntimeException("当前不在事务中");
        }

        current.failed = current.failed || fail;

        if (current.isFailed()) {
            current.rollback(true);
        } else {
            current.commit();
        }

        threadLocal.set(null);

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
        int count = 0;
        Transaction current = begin();
        try {
            while (true) {
                count++;
                logger.debug("当前第{}次执行事务{}", count, current.getId());
                task.run();
                current.lock();
                if (current.isConflict()) {
                    //有冲突，其他事务也修改了数据
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


    private void lock() {
        List<Data> dataList = new ArrayList<>();
        for (Data data : versionLogs.keySet()) {
            dataList.add(data);
        }

        //排序加锁，保证不同事务按照同一顺序竞争锁，防止死锁
        Collections.sort(dataList);
        for (Data data : dataList) {
            locks.add(data.getLock());
        }

        for (Lock lock : locks) {
            lock.lock();
        }
    }

    private void unlock() {
        for (Lock lock : locks) {
            lock.unlock();
        }
        locks.clear();
    }


    private boolean isConflict() {
        for (VersionLog versionLog : versionLogs.values()) {
            if (versionLog.isConflict()) {
                return true;
            }
        }
        for (DataLog dataLog : dataLogs.values()) {
            if (dataLog.isConflict()) {
                return true;
            }
        }
        return false;
    }


    private void commit() {
        try {
            for (VersionLog versionLog : versionLogs.values()) {
                versionLog.commit();
            }
            for (RootLog rootLog : rootLogs.values()) {
                rootLog.commit();
            }
            for (FieldLog fieldLog : fieldLogs.values()) {
                fieldLog.commit();
            }

            versionLogs.clear();
            fieldLogs.clear();
            rootLogs.clear();
            failed = false;
        } finally {
            unlock();
        }

        logger.debug("提交事务{}", getId());
    }

    /**
     * 执行失败或者和其他事务冲突时回滚事务
     *
     * @param end 是不是结束事务的回滚
     */
    private void rollback(boolean end) {
        try {
            versionLogs.clear();
            fieldLogs.clear();
            rootLogs.clear();
            failed = false;
        } finally {
            unlock();
        }

        logger.debug("回滚事务{}", getId());
    }

}
