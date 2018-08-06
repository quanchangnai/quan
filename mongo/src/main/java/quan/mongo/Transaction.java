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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 事务
 * Created by quanchangnai on 2017/6/2.
 */
public class Transaction {

    protected final Logger logger = LogManager.getLogger(getClass());

    private static ThreadLocal<Transaction> threadLocal = new ThreadLocal<>();

    /**
     * 事务支持是否已经开启
     */
    private static AtomicBoolean enabled = new AtomicBoolean(false);

    /**
     * 事务是否失败
     */
    private boolean failed;

    /**
     * 当前事务管理的MappingData
     */
    private Set<MappingData> mappingDatas = new HashSet();

    /**
     * 事务提交后需要执行的任务
     */
    private List<Runnable> afterCommitTasks = new ArrayList<>();


    /**
     * 开启事务支持
     */
    public static void enable() {
        if (enabled.get()) {
            return;
        }
        Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
                .type(ElementMatchers.any())
                .transform(new Transformer())
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
//                .enableNativeMethodPrefix("original$")
                .installOn(instrumentation);

        enabled.set(true);
    }

    /**
     * 开始事务
     */
    public static void start() {
        if (current() == null) {
            threadLocal.set(new Transaction());
        }
    }

    /**
     * 执行成功时提交事务
     */
    public void commit() {
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
    public void rollback() {
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
