package quan.mongo;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 事务
 * Created by quanchangnai on 2018/8/6.
 */
public class Transaction {

    /**
     * logger
     */
    protected final Logger logger = LogManager.getLogger(getClass());

    /**
     * 保存事务为线程本地变量
     */
    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    /**
     * 是否已经开启声明式事务支持
     */
    private static boolean declarative;

    /**
     * 事务是否失败
     */
    private boolean failed;

    /**
     * 事务进入计数
     */
    private int enterCount;

    /**
     * 当前事务管理的MappingData
     */
    private Set<MappingData> mappingDatas = new HashSet();

    /**
     * 事务提交后需要执行的任务
     */
    private List<Runnable> afterCommitTasks = new ArrayList<>();

    private Transaction() {
    }

    /**
     * 开启声明式事务支持
     */
    public static synchronized void declarative() {
        if (declarative) {
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
        declarative = true;
    }

    /**
     * 开始事务，进入次数加1
     */
    public static void start() {
        Transaction current = current();
        if (current == null) {
            current = new Transaction();
            threadLocal.set(current);
        }
        current.enterCount++;
    }

    /**
     * 结束当前事务，进入次数减1，当值减到0时提交或回滚事务
     *
     * @param fail 是否失败
     */
    public static void end(boolean fail) {
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
     * 编程式事务
     *
     * @param task 在事务中执行的任务
     */
    public static void execute(Runnable task) {
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
     * 执行成功时提交事务
     */
    private void commit() {
        System.err.println("commit==================");
        for (MappingData mappingData : mappingDatas) {
            mappingData.commit();
        }

        mappingDatas.clear();
        threadLocal.set(null);

        for (Runnable task : afterCommitTasks) {
            try {
                task.run();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * 执行失败时回滚事务
     */
    private void rollback() {
        System.err.println("rollback==================");
        for (MappingData mappingData : mappingDatas) {
            mappingData.rollback();
        }
        mappingDatas.clear();
        threadLocal.set(null);
    }

    /**
     * 添加MappingData到受事务管理的集合里
     *
     * @param mappingData
     */
    void addMappingData(MappingData mappingData) {
        if (mappingData == null) {
            return;
        }
        mappingDatas.add(mappingData);
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
        Transaction current = current();
        if (current != null) {
            current.failed = true;
        }
    }
}
