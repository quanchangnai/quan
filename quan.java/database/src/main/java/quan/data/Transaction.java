package quan.data;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.field.Field;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.Boolean.FALSE;

/**
 * 事务实现，多线程并发时需要自己加锁，否则隔离级别就是读已提交<br/>
 * Created by quanchangnai on 2019/5/16.
 */
public class Transaction {

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    /**
     * 在事务外是否能修改数据，全局范围生效
     */
    private static boolean globalOptional = false;

    /**
     * 在事务外是否能修改数据，仅对线程范围生效
     */
    private static ThreadLocal<Boolean> localOptional = ThreadLocal.withInitial(FALSE::booleanValue);

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
    private List<Listener> listeners = new ArrayList<>();

    /**
     * @see Savepoint
     */
    @SuppressWarnings("SpellCheckingInspection")
    private Savepoint[] savepoints = new Savepoint[8];

    /**
     * 事务深度，开启一个内嵌事务加一
     */
    private int depth = 1;

    /**
     * @see Transaction#globalOptional
     */
    public static void setGlobalOptional(boolean globalOptional) {
        Transaction.globalOptional = globalOptional;
    }

    /**
     * @see Transaction#localOptional
     */
    public static void setLocalOptional(boolean localOptional) {
        Transaction.localOptional.set(localOptional);
    }

    public static boolean isOptional() {
        return globalOptional || localOptional.get();
    }

    void setDataLog(Data<?> data, Data.Log log) {
        dataLogs.put(data, log);
    }

    Data.Log getDataLog(Data<?> data) {
        Data.Log log = dataLogs.get(data);
        if (log != null) {
            return log;
        }
        for (int i = depth - 2; i >= 0; i--) {
            log = savepoints[i].dataLogs.get(data);
            if (log != null) {
                return log;
            }
        }
        return null;
    }

    void setFieldLog(Field field, Object value, Data<?> data) {
        fieldLogs.put(field, value);
        if (data != null && data.writer != null && data.state != null && !dataLogs.containsKey(data)) {
            dataLogs.put(data, new Data.Log(data.writer, data.state));
        }
    }

    Object getFieldLog(Field field) {
        Object log = fieldLogs.get(field);
        if (log != null) {
            return log;
        }
        for (int i = depth - 2; i >= 0; i--) {
            log = savepoints[i].fieldLogs.get(field);
            if (log != null) {
                return log;
            }
        }
        return null;
    }

    void setRootLog(Node node, Data<?> root) {
        rootLogs.put(node, root);
    }

    Data<?> getRootLog(Node node) {
        Data<?> log = rootLogs.get(node);
        if (log != null) {
            return log;
        }
        for (int i = depth - 2; i >= 0; i--) {
            log = savepoints[i].rootLogs.get(node);
            if (log != null) {
                return log;
            }
        }
        return null;
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
            error();
        }
        return transaction;
    }

    public static void error() {
        throw new IllegalStateException("当前不在事务中");
    }

    /**
     * 获取当前事务
     */
    public static Transaction get() {
        return threadLocal.get();
    }

    /**
     * @see #execute(Runnable, boolean)
     */
    public static void execute(Runnable task) {
        execute(task, false);
    }

    /**
     * 在事务中执行任务
     *
     * @param task   执行逻辑
     * @param nested 如果在事务中再开启事务是开启内嵌事务还是直接使用当前事务
     */
    public static void execute(Runnable task, boolean nested) {
        Transaction transaction = Transaction.begin(nested);
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
     * @see #execute(Runnable, boolean)
     */
    public static <R> R execute(Supplier<R> task) {
        return execute(task, false);
    }

    /**
     * 在事务中执行任务,带返回结果
     *
     * @see #execute(Runnable, boolean)
     */
    public static <R> R execute(Supplier<R> task, boolean nested) {
        Transaction transaction = Transaction.begin(nested);
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
     * 开始事务
     */
    private static Transaction begin(boolean nested) {
        Transaction transaction = threadLocal.get();
        if (transaction == null) {
            transaction = new Transaction();
            threadLocal.set(transaction);
        } else if (nested) {
            save(transaction);
        } else {
            throw new IllegalStateException("当前已经在事务中了");
        }
        return transaction;
    }

    /**
     * 结束当前事务
     */
    private static void end(Transaction transaction) {
        if (transaction.depth > 1) {
            restore(transaction);
            return;
        }

        //清空当前线程持有的事务对象
        threadLocal.set(null);

        //事务执行成功，提交事务
        if (!transaction.failed) {
            transaction.commit();
        }

        //执行事务结束后的特殊任务
        int when = transaction.failed ? Listener.WHEN_FAILED : Listener.WHEN_SUCCEEDED;
        for (Listener task : transaction.listeners) {
            if ((task.when & when) == when) {
                try {
                    task.task.run();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 保存外层事务，开启内嵌事务
     */
    private static void save(Transaction transaction) {
        if (transaction.depth > transaction.savepoints.length) {
            throw new IllegalStateException("事务嵌套层数(" + (transaction.depth + 1) + ")太深了");
        }

        Savepoint savepoint = new Savepoint();
        savepoint.failed = transaction.failed;
        savepoint.dataLogs = transaction.dataLogs;
        savepoint.rootLogs = transaction.rootLogs;
        savepoint.fieldLogs = transaction.fieldLogs;
        savepoint.listeners = transaction.listeners;

        transaction.savepoints[transaction.depth - 1] = savepoint;
        transaction.depth++;

        transaction.failed = false;
        transaction.dataLogs = new HashMap<>();
        transaction.rootLogs = new HashMap<>();
        transaction.fieldLogs = new HashMap<>();
        transaction.listeners = new ArrayList<>();
    }

    /**
     * 结束内嵌事务，恢复外层事务
     */
    private static void restore(Transaction transaction) {
        Savepoint savepoint = transaction.savepoints[--transaction.depth - 1];
        transaction.savepoints[transaction.depth - 1] = null;

        if (!transaction.failed) {
            savepoint.dataLogs.putAll(transaction.dataLogs);
            savepoint.rootLogs.putAll(transaction.rootLogs);
            savepoint.fieldLogs.putAll(transaction.fieldLogs);
        }

        for (Listener listener : transaction.listeners) {
            if (transaction.failed) {
                if (listener.when == Listener.WHEN_SUCCEEDED) {
                    continue;
                }
                if (listener.when == Listener.WHEN_FAILED) {
                    listener.when = Listener.WHEN_FINISHED;
                }
            }
            savepoint.listeners.add(listener);
        }

        transaction.dataLogs = savepoint.dataLogs;
        transaction.rootLogs = savepoint.rootLogs;
        transaction.fieldLogs = savepoint.fieldLogs;
        transaction.failed = savepoint.failed;
        transaction.listeners = savepoint.listeners;
    }


    /**
     * 回滚当前事务
     */
    public static void rollback() {
        //标记事务为失败状态
        check().failed = true;
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
                logger.error("内存事务提交后写数据库出错", e);
            }
        }

    }

    /**
     * 在当前事务执行成功之后再执行特殊任务
     */
    public static void onSucceeded(Runnable task) {
        check().listeners.add(new Listener(task, Listener.WHEN_SUCCEEDED));
    }

    /**
     * 在当前事务执行失败之后再执行特殊任务
     */
    public static void onFailed(Runnable task) {
        check().listeners.add(new Listener(task, Listener.WHEN_FAILED));
    }

    /**
     * 在当前事务执行完成(不管成功或者失败)之后再执行特殊任务
     */
    public static void onFinished(Runnable task) {
        check().listeners.add(new Listener(task, Listener.WHEN_FINISHED));
    }


    static class Listener {

        Runnable task;

        int when;

        //事务成功后执行
        static final int WHEN_SUCCEEDED = 1;

        //事务失败后执行
        static final int WHEN_FAILED = 2;

        //事务成功或者失败后都要执行
        static final int WHEN_FINISHED = 3;

        public Listener(Runnable task, int when) {
            this.task = task;
            this.when = when;
        }
    }

}
