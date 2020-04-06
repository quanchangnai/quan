package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 基于乐观锁的事务
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
     * 记录数据是否有修改
     */
    private Set<Data> dataLogs = new LinkedHashSet<>();

    /**
     * 记录节点的根
     */
    private Map<Node, RootLog> rootLogs = new HashMap<>();

    /**
     * 记录字段值
     */
    private Map<Field, FieldLog> fieldLogs = new HashMap<>();

    /**
     * 事务成功执行提交后的回调，不随事务结束而清除
     */
    private static List<CommitListener> commitListeners = new ArrayList<>();

    /**
     * 事务执行成功之后需要执行的任务
     */
    private List<Runnable> successTasks = new ArrayList<>();

    /**
     * 事务执行失败之后需要执行的任务
     */
    private List<Runnable> failedTasks = new ArrayList<>();

    public boolean isFailed() {
        return failed;
    }


    public void addDataLog(Data data) {
        dataLogs.add(data);
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

    /**
     * 当前是不是处于事务之中
     */
    public static boolean isInside() {
        return threadLocal.get() != null;
    }


    /**
     * 获取当前事务
     *
     * @param validate 校验当前是否处于事务之中
     */
    public static Transaction get(boolean validate) {
        Transaction transaction = threadLocal.get();
        if (validate && transaction == null) {
            throw new IllegalStateException("当前不在事务中");
        }
        return transaction;
    }

    /**
     * 获取当前事务
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
        Transaction transaction = get(true);
        Set<Data> changes = new LinkedHashSet<>();

        try {
            transaction.failed = transaction.failed || failed;
            if (!transaction.failed) {
                transaction.commit();
                changes.addAll(transaction.dataLogs);
                changes = Collections.unmodifiableSet(changes);
            }
            transaction.clearLogs();
        } finally {
            threadLocal.set(null);
            if (transaction.failed) {
                executeAfterTasks(transaction.failedTasks);
            } else {
                for (CommitListener commitListener : commitListeners) {
                    try {
                        commitListener.onCommit(changes);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
                executeAfterTasks(transaction.successTasks);
            }
        }
    }

    /**
     * 打断事务,事务将失败回滚
     */
    public static void breakdown() {
        Transaction.get(true).failed = true;
        throw new BreakdownException();
    }

    /**
     * 在事务中执行任务，执行线程为调用方法的当前线程
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
     * @param task 事务逻辑
     */
    public static void outsideExecute(Task task) {
        if (Transaction.isInside()) {
            throw new IllegalStateException("当前已经在事务中了");
        }

        Transaction.begin();
        boolean failed = false;

        try {
            failed = task.run();
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
     * 清空当前事务日志，事务执行结束或者事务逻辑发生冲突时需要
     */
    private void clearLogs() {
        dataLogs.clear();
        fieldLogs.clear();
        rootLogs.clear();
    }

    /**
     * 事务提交
     */
    private void commit() {
        for (RootLog rootLog : rootLogs.values()) {
            rootLog.commit();
        }
        for (FieldLog fieldLog : fieldLogs.values()) {
            fieldLog.commit();
        }

    }

    private static void executeAfterTasks(List<Runnable> afterTasks) {
        for (Runnable afterTask : afterTasks) {
            try {
                afterTask.run();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        afterTasks.clear();
    }

    public static void listenCommit(CommitListener listener) {
        commitListeners.add(listener);
    }


    /**
     * 在事务执行完成之后执行的特殊任务 run task after commit
     *
     * @param task      需要执行的任务
     * @param committed true:事务提交之后执行,false:事务回滚之后执行
     */
    public static void runTaskAfterCommit(Runnable task, boolean committed) {
        Transaction transaction = Transaction.get(true);
        if (committed) {
            transaction.successTasks.add(task);
        } else {
            transaction.failedTasks.add(task);
        }
    }

    /**
     * 打断事务的异常，用于标记事务失败，不会往外抛出
     */
    static class BreakdownException extends RuntimeException {
    }


}
