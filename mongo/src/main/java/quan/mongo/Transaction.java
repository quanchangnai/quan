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
     * 是否已经开启事务支持
     */
    private static boolean enabled;

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
     * 开启事务支持
     */
    public static synchronized void enable() {
        if (enabled) {
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

    /**
     * 检测是否开启了事务支持
     */
    private static void checkEnabled() {
        if (!enabled) {
            throw new UnsupportedOperationException("未开启事务支持");
        }
    }

    /**
     * 开始事务，重复开始时事务进入次数加1
     */
    public static void start() {
        checkEnabled();
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
     * @param fail 是否失败
     */
    public static void end(boolean fail) {
        checkEnabled();
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
        checkEnabled();
        return threadLocal.get();
    }

    /**
     * 标记当前事务为失败状态
     */
    public static void fail() {
        checkEnabled();
        Transaction current = current();
        if (current != null) {
            current.failed = true;
        }
    }
}
