package quan.transaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.transaction.field.TypeField;
import quan.transaction.log.DataLog;
import quan.transaction.log.FieldLog;

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
    private static final Logger logger = LogManager.getLogger(Transaction.class);

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    private static final AtomicLong idGenerator = new AtomicLong();

    /**
     * 事务ID
     */
    private long id;


    /**
     * 事务是否失败
     */
    private boolean failed;

    private List<Lock> locks = new ArrayList<>();

    private Map<MappingData, DataLog> dataLogs = new HashMap<>();

    /**
     * 字段日志
     */
    private Map<TypeField, FieldLog> fieldLogs = new HashMap<>();

    {
        this.id = idGenerator.incrementAndGet();
    }

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

    public void addDataLog(MappingData data) {
        if (dataLogs.containsKey(data)) {
            return;
        }
        DataLog dataLog = new DataLog(data);
        dataLogs.put(dataLog.getData(), dataLog);
    }

    public DataLog getDataLog(MappingData data) {
        return dataLogs.get(data);
    }

    public void addFieldLog(FieldLog fieldLog) {
        fieldLogs.put(fieldLog.getField(), fieldLog);
    }

    public FieldLog getFieldLog(TypeField field) {
        return fieldLogs.get(field);
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
    public static void begin() {
        Transaction current = current();
        if (current == null) {
            current = new Transaction();
            threadLocal.set(current);
        } else {
            throw new RuntimeException("当前已经在事务中了");
        }
    }

    /**
     * 结束当前事务
     *
     * @param fail 事务是否失败
     */
    public static void end(boolean fail) {
        Transaction current = current();
        if (current == null) {
            throw new RuntimeException("当前不在事务中");
        }

        current.failed = current.failed || fail;

        if (current.isFailed()) {
            current.rollback();
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
        begin();
        Transaction current = current();
        try {
            while (true) {
                count++;
                logger.debug("当前第{}次执行事务{}", count, current.getId());
                task.run();
                current.lock();
                if (current.checkConflict()) {
                    //有冲突，其他事务也修改了数据
                    current.rollback();
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
        List<MappingData> dataList = new ArrayList<>();
        for (MappingData data : dataLogs.keySet()) {
            dataList.add(data);
        }

        //排序加锁，保证解锁顺序一致
        Collections.sort(dataList);
        for (MappingData data : dataList) {
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


    private boolean checkConflict() {
        boolean conflict = false;
        for (DataLog dataLog : dataLogs.values()) {
            if (dataLog.getData().getVersion() != dataLog.getVersion()) {
                conflict = true;
                break;
            }
        }

        return conflict;
    }


    private void commit() {
        try {
            for (DataLog dataLog : dataLogs.values()) {
                dataLog.commit();
            }
            for (TypeField field : fieldLogs.keySet()) {
                FieldLog fieldLog = fieldLogs.get(field);
                fieldLog.commit();
            }

            dataLogs.clear();
            fieldLogs.clear();
            failed = false;
        } finally {
            unlock();
        }

        logger.debug("提交事务{}", getId());
    }

    /**
     * 执行失败或者和其他事务冲突时回滚事务
     */
    private void rollback() {
        try {
            dataLogs.clear();
            fieldLogs.clear();
            failed = false;
        } finally {
            unlock();
        }

        logger.debug("回滚事务{}", getId());
    }
}
