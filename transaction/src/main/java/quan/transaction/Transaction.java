package quan.transaction;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.transaction.field.Field;
import quan.transaction.log.FieldLog;
import quan.transaction.log.RootLog;
import quan.transaction.log.VersionLog;

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
    private long id = idGenerator.incrementAndGet();

    /**
     * 是否已经开启事务支持
     */
    private static volatile boolean enabled;

    /**
     * 事务是否失败
     */
    private boolean failed;

    private List<Lock> locks = new ArrayList<>();

    /**
     * 版本日志，记录版本号的变化
     */
    private Map<MappingData, VersionLog> versionLogs = new HashMap<>();

    /**
     * Root日志，记录Root的变化
     */
    private Map<BeanData, RootLog> rootLogs = new HashMap<>();

    /**
     * 字段日志，记录字段值的变化
     */
    private Map<Field, FieldLog> fieldLogs = new HashMap<>();

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
        } else {
            throw new RuntimeException("当前不在事务中");
        }
    }

    public static void breakdown() {
        fail();
        throw new RuntimeException("事务被标记为失败状态");
    }

    public void addVersionLog(MappingData data) {
        if (versionLogs.containsKey(data)) {
            return;
        }
        VersionLog versionLog = new VersionLog(data);
        versionLogs.put(versionLog.getData(), versionLog);
    }

    public VersionLog getVersionLog(MappingData data) {
        return versionLogs.get(data);
    }

    public void addFieldLog(FieldLog fieldLog) {
        fieldLogs.put(fieldLog.getField(), fieldLog);
    }

    public FieldLog getFieldLog(Field field) {
        return fieldLogs.get(field);
    }

    public void addRootLog(RootLog rootLog) {
        rootLogs.put(rootLog.getBeanData(), rootLog);
    }

    public RootLog getRootLog(BeanData beanData) {
        return rootLogs.get(beanData);
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
                if (current.conflict()) {
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
        List<MappingData> dataList = new ArrayList<>();
        for (MappingData data : versionLogs.keySet()) {
            dataList.add(data);
        }

        //排序加锁，保证不同事务按照同一顺序竞争锁，防止死锁
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


    private boolean conflict() {
        for (VersionLog versionLog : versionLogs.values()) {
            if (versionLog.conflict()) {
                return true;
            }
        }
        return false;
    }


    private void commit() {
        try {
            Set<MappingData> writes = new HashSet<>();
            for (VersionLog versionLog : versionLogs.values()) {
                versionLog.commit();
                writes.add(versionLog.getData());
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


    /**
     * 开启声明式事务支持
     */
    public static synchronized void enable() {
        if (enabled) {
            logger.error("事务支持已开启");
            return;
        }

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module) -> builder
                .method(ElementMatchers.isAnnotatedWith(Transactional.class))
                .intercept(MethodDelegation.to(TransactionInterceptor.class));

        new AgentBuilder.Default()
                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
                .enableNativeMethodPrefix("original$")
                .type(ElementMatchers.isAnnotatedWith(Transactional.class))
                .transform(transformer)
                .installOn(ByteBuddyAgent.install());

        enabled = true;
    }
}
