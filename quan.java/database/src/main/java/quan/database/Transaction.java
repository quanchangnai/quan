package quan.database;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.field.Field;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
     * 记录被修改过的数据
     */
    private Set<Data<?>> dataLogs = new LinkedHashSet<>();

    /**
     * 记录节点的根
     */
    private Map<Node, Data<?>> rootLogs = new HashMap<>();

    /**
     * 记录字段值
     */
    private Map<Field, Object> fieldLogs = new HashMap<>();

    /**
     * 数据更新器
     */
    private static LinkedHashSet<DataUpdater> updaters = new LinkedHashSet<>();

    /**
     * 事务执行结束之后再执行的特殊任务
     */
    private LinkedHashMap<Runnable, Boolean> endTasks = new LinkedHashMap<>();


    public boolean isFailed() {
        return failed;
    }

    void setFieldLog(Field field, Object value, Data<?> data) {
        fieldLogs.put(field, value);
        if (data != null) {
            dataLogs.add(data);
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
     * 当前是不是处于事务之中
     */
    public static boolean isInside() {
        return threadLocal.get() != null;
    }


    /**
     * 获取当前事务
     *
     * @param check 检测当前是否处于事务之中
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
        for (Runnable task : transaction.endTasks.keySet()) {
            if (transaction.failed != transaction.endTasks.get(task)) {
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
     * 打断事务,事务将失败回滚，但不会打印异常信息
     */
    public static void breakdown() {
        Transaction.get(true).failed = true;
        throw new BreakdownException();
    }

    /**
     * 标记事务失败
     */
    public static void fail() {
        Transaction.get(true).failed = true;
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
            if (!(e instanceof BreakdownException)) {
                throw e;
            }
        } finally {
            end(transaction);
        }
    }

    /**
     * 事务提交
     */
    private void commit() {
        for (Node node : rootLogs.keySet()) {
            node.commit(rootLogs.get(node));
        }

        for (Field field : fieldLogs.keySet()) {
            field.commit(fieldLogs.get(field));
        }

        List<Data<?>> updates = Collections.unmodifiableList(new ArrayList<>(dataLogs));
        for (DataUpdater updater : updaters) {
            try {
                updater.update(updates);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 添加数据更新器
     */
    static void addUpdater(DataUpdater updater) {
        updaters.add(updater);
    }

    /***
     * 在当前事务执行提交之后再执行特殊任务
     */
    public static void runAfterCommit(Runnable task) {
        Transaction.get(true).endTasks.put(task, true);
    }

    /**
     * 在当前事务执行回滚之后再执行特殊任务
     */
    public static void runAfterRollback(Runnable task) {
        Transaction.get(true).endTasks.put(task, false);
    }


    /**
     * 创建实现了声明式事务的代理对象
     *
     * @param clazz     目标类型
     * @param argTypes  构造方法参数类型
     * @param argValues 构造方法参数值
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxy(Class<T> clazz, Class<?>[] argTypes, Object[] argValues) {
        Objects.requireNonNull(clazz, "目标类型不能为空");

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallbackFilter(method -> method.isAnnotationPresent(Transactional.class) ? 0 : 1);

        MethodInterceptor interceptor = (obj, method, args, proxy) -> {
            AtomicReference<Throwable> exception = new AtomicReference<>();
            AtomicReference<Object> result = new AtomicReference<>();
            //在事务中执行原始方法
            Transaction.execute(() -> {
                try {
                    result.set(proxy.invokeSuper(obj, args));
                } catch (Throwable e) {
                    if (e instanceof BreakdownException) {
                        throw (BreakdownException) e;
                    } else {
                        exception.set(e);
                    }
                }
            });

            if (exception.get() != null) {
                throw exception.get();
            } else {
                return result.get();
            }
        };

        enhancer.setCallbacks(new Callback[]{interceptor, NoOp.INSTANCE});

        if (argTypes == null && argValues == null) {
            return (T) enhancer.create();
        } else {
            assert argTypes != null;
            return (T) enhancer.create(argTypes, argValues);
        }
    }

    /**
     * 创建实现了声明式事务的代理对象
     *
     * @param clazz 目标类型
     */
    public static <T> T proxy(Class<T> clazz) {
        return proxy(clazz, null, null);
    }

    /**
     * 打断事务的异常，用于标记事务失败，不会往外抛出
     */
    static class BreakdownException extends RuntimeException {
    }

}
