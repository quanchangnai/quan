package quan.data;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.field.Field;

import java.util.*;
import java.util.function.Supplier;

/**
 * 事务实现，多线程并发时需要自己加锁，否则隔离级别就是读已提交
 * Created by quanchangnai on 2019/5/16.
 */
public class Transaction {

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    /**
     * 事务是否已失败
     */
    private boolean failed;

    /**
     * 记录数据的状态(插入、更新、删除)和使用的写入器
     */
    private Map<Data<?>, Data.Log> dataLogs = new LinkedHashMap<>();

    /**
     * 记录节点的根
     */
    private Map<Node, Data<?>> rootLogs = new HashMap<>();

    /**
     * 记录字段值
     */
    private Map<Field, Object> fieldLogs = new HashMap<>();

    /**
     * 在事务执行结束之后再执行的特殊任务
     */
    private LinkedHashMap<Runnable, Boolean> afterTasks = new LinkedHashMap<>();

    void setDataLog(Data<?> data, Data.Log log) {
        dataLogs.put(data, log);
    }

    Data.Log getDataLog(Data<?> data) {
        return dataLogs.get(data);
    }

    void setFieldLog(Field field, Object value, Data<?> data) {
        fieldLogs.put(field, value);
        if (data != null && data.writer != null && data.state != null && !dataLogs.containsKey(data)) {
            dataLogs.put(data, new Data.Log(data.writer, data.state));
        }
    }

    Object getFieldLog(Field field) {
        return fieldLogs.get(field);
    }

    void setRootLog(Node node, Data<?> root) {
        rootLogs.put(node, root);
    }

    Data<?> getRootLog(Node node) {
        return rootLogs.get(node);
    }

    /**
     * 判断当前是否处于事务之中
     */
    public static boolean isInside() {
        return threadLocal.get() != null;
    }

    /**
     * 检测当前是否处于事务之中，如果当前在事务之中则返回当前事务，否则报错
     */
    public static Transaction check() {
        Transaction transaction = threadLocal.get();
        if (transaction == null) {
            throw new IllegalStateException("当前不在事务中");
        }
        return transaction;
    }

    /**
     * 获取当前事务
     */
    public static Transaction get() {
        return threadLocal.get();
    }

    /**
     * 开始事务
     */
    private static Transaction begin() {
        Transaction transaction = threadLocal.get();
        if (transaction == null) {
            transaction = new Transaction();
            threadLocal.set(transaction);
        } else {
            throw new IllegalStateException("当前已经在事务中了");
        }
        return transaction;
    }

    /**
     * 在事务中执行任务
     */
    public static void execute(Runnable task) {
        if (threadLocal.get() != null) {
            task.run();
            return;
        }

        Transaction transaction = Transaction.begin();
        try {
            task.run();
        } catch (Throwable e) {
            transaction.failed = true;
            throw e;
        } finally {
            end(transaction);
        }
    }

    /**
     * 在事务中执行任务
     */
    public static <R> R execute(Supplier<R> task) {
        if (threadLocal.get() != null) {
            return task.get();
        }

        Transaction transaction = Transaction.begin();
        try {
            return task.get();
        } catch (Throwable e) {
            transaction.failed = true;
            throw e;
        } finally {
            end(transaction);
        }
    }


    /**
     * 结束当前事务
     */
    private static void end(Transaction transaction) {
        //清空当前线程持有的事务对象
        threadLocal.set(null);

        //事务执行成功，提交事务
        if (!transaction.failed) {
            transaction.commit();
        }

        //执行事务结束后的特殊任务
        for (Runnable task : transaction.afterTasks.keySet()) {
            if (transaction.failed == transaction.afterTasks.get(task)) {
                continue;
            }
            try {
                task.run();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 回滚当前事务
     */
    public static void rollback() {
        //标记事务为失败状态
        Transaction.check().failed = true;
    }

    /**
     * 提交事务
     */
    private void commit() {
        for (Node node : rootLogs.keySet()) {
            node.commit(rootLogs.get(node));
        }

        for (Field field : fieldLogs.keySet()) {
            field.commit(fieldLogs.get(field));
        }

        Map<DataWriter, Triple<List<Data<?>>, List<Data<?>>, List<Data<?>>>> writers = new HashMap<>();
        for (Data<?> data : dataLogs.keySet()) {
            Data.Log log = dataLogs.get(data);
            data.commit(log);

            if (log.writer == null || log.state == null) {
                continue;
            }

            Triple<List<Data<?>>, List<Data<?>>, List<Data<?>>> writings = writers.computeIfAbsent(log.writer, w -> Triple.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            switch (log.state) {
                case INSERTION:
                    writings.getLeft().add(data);
                    break;
                case UPDATE:
                    writings.getMiddle().add(data);
                    break;
                case DELETION:
                    writings.getRight().add(data);
                    break;
            }
        }

        for (DataWriter writer : writers.keySet()) {
            try {
                Triple<List<Data<?>>, List<Data<?>>, List<Data<?>>> writings = writers.get(writer);
                writer.write(writings.getLeft(), writings.getMiddle(), writings.getRight());
            } catch (Exception e) {
                logger.error("事务提交后写数据库出错", e);
            }
        }

    }

    /***
     * 在当前事务执行提交之后再执行特殊任务
     */
    public static void runAfterCommit(Runnable task) {
        Transaction.check().afterTasks.put(task, true);
    }

    /**
     * 在当前事务执行回滚之后再执行特殊任务
     */
    public static void runAfterRollback(Runnable task) {
        Transaction.check().afterTasks.put(task, false);
    }

}
