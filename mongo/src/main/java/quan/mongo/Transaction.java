package quan.mongo;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 可重入的事务，基于单线程实现
 * Created by quanchangnai on 2018/8/6.
 */
public final class Transaction {

    /**
     * 日志
     */
    private static final Logger logger = LogManager.getLogger(Transaction.class);

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    /**
     * 是否已经开启事务支持
     */
    private static volatile boolean enabled;

    /**
     * 事务是否失败
     */
    private boolean failed;

    /**
     * 事务进入计数
     */
    private int enterCount;

    /**
     * 当前事务修改过的MappingData
     */
    private Set<MappingData> writeData = new HashSet();

    /**
     * 后置任务，事务提交后需要执行的任务，事务回滚不会执行
     */
    private List<Runnable> postTasks = new ArrayList<>();

    private Transaction() {
    }

    /**
     * 开启声明式事务支持
     */
    public static synchronized void enable() {
        if (enabled) {
            logger.error("事务支持已开启");
            return;
        }

        Instrumentation instrumentation = ByteBuddyAgent.install();

        new AgentBuilder.Default()
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
                .enableNativeMethodPrefix("original$")
                .type(ElementMatchers.hasAnnotation(ElementMatchers.annotationType(Transactional.class)))
                .transform(new Transformer())
                .installOn(instrumentation);

        enabled = true;
    }

    private static boolean checkEnabled() {
        if (!enabled) {
            logger.error("事务支持未开启");
            return false;
        }
        return true;
    }

    /**
     * 开始事务，如果已经在事务中了，则事务进入次数加1
     */
    public static void start() {
        if (!checkEnabled()) {
            return;
        }
        Transaction current = current();
        if (current == null) {
            current = new Transaction();
            threadLocal.set(current);
        }
        current.enterCount++;
    }

    /**
     * 结束当前事务，事务进入次数减1，当值减到0时提交或回滚事务
     *
     * @param fail 事务是否失败
     */
    public static void end(boolean fail) {
        if (!checkEnabled()) {
            return;
        }
        Transaction current = current();
        if (current == null) {
            return;
        }
        current.failed = current.failed || fail;
        if (--current.enterCount < 1) {
            if (current.isFailed()) {
                current.rollback();
            } else {
                current.commit();
            }
        }
    }

    /**
     * 编程式事务中执行任务
     *
     * @param task
     */
    public static void execute(Runnable task) {
        if (!checkEnabled()) {
            return;
        }
        boolean fail = false;
        start();
        try {
            task.run();
        } catch (Throwable e) {
            fail = true;
            throw e;
        } finally {
            end(fail);
        }
    }

    /**
     * 编程式事务
     *
     * @param task 在事务中执行的任务
     * @param <T>  任务的返回结果
     * @return
     */
    public static <T> T execute(Supplier<T> task) {
        if (!checkEnabled()) {
            return null;
        }
        boolean fail = false;
        start();
        try {
            return task.get();
        } catch (Throwable e) {
            fail = true;
            throw e;
        } finally {
            end(fail);
        }
    }

    /**
     * 添加后置任务
     *
     * @param task 只会在事务提交后执行
     */
    public void addPostTask(Runnable task) {
        postTasks.add(task);
    }

    /**
     * 执行成功时提交事务
     */
    private void commit() {
        for (MappingData data : writeData) {
            data.commit();
            if (data.isNewState()) {
                //异步插入
                System.err.println("异步插入==================");
                data.encode();
            } else if (data.isNormalState()) {
                //异步更新
                System.err.println("异步更新==================");
                data.encode();
            }
        }

        for (Runnable task : postTasks) {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("执行后置任务异常", e);
            }
        }

        writeData.clear();
        postTasks.clear();
        threadLocal.set(null);
    }

    /**
     * 执行失败时回滚事务
     */
    private void rollback() {
        System.err.println("rollback==================");
        for (MappingData data : writeData) {
            data.rollback();
        }
        writeData.clear();
        postTasks.clear();
        threadLocal.set(null);
    }

    /**
     * 事务管理MappingData
     *
     * @param data
     */
    void addWriteData(MappingData data) {
        if (!checkEnabled()) {
            return;
        }
        if (data == null) {
            return;
        }
        writeData.add(data);
    }

    public boolean isFailed() {
        return failed;
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
     * 标记当前事务为失败状态
     */
    public static void fail() {
        if (!checkEnabled()) {
            return;
        }
        Transaction current = current();
        if (current != null) {
            current.failed = true;
        }
    }


    /**
     * 实现事务的字节码转换器
     */
    private static class Transformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.method(ElementMatchers.isAnnotatedWith(Transactional.class)).intercept(Advice.to(AdviceImpl.class));
        }
    }

    /**
     * 事务通知实现
     */
    private static class AdviceImpl {

        @Advice.OnMethodEnter
        public static void onMethodEnter() {
            Transaction.start();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void onMethodExit(@Advice.Thrown Throwable thrown) {
            Transaction.end(thrown != null);
        }

    }

}
