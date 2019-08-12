package quan.database;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

/**
 * 事务工具类
 * Created by quanchangnai on 2019/7/2.
 */
public class Transactions {

    private static final Logger logger = LoggerFactory.getLogger(Transactions.class);


    /**
     * 创建代理子类方式实现声明式事务，支持父类热加载
     *
     * @param clazz 目标类型
     * @param <T>
     * @return
     */
    public synchronized static <T> Class<? extends T> subclass(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        MethodDelegation methodDelegation = MethodDelegation.to(TransactionDelegation.class);

        ElementMatcher.Junction<MethodDescription> methodMatcher = ElementMatchers.isAnnotatedWith(Transactional.class);

        return new ByteBuddy()
                .subclass(clazz)
                .method(methodMatcher)
                .intercept(methodDelegation)
                .make()
                .load(clazz.getClassLoader())
                .getLoaded();
    }

    /**
     * 创建子类代理对象方式实现声明式事务，支持父类热加载
     *
     * @param clazz    目标类型
     * @param executor 异步线程池为空时将同步执行,同步执行并能返回正确结果，异步执行最外层原始方法返回值将会丢失
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> clazz, Executor executor) {
        Objects.requireNonNull(clazz);
        return (T) Enhancer.create(clazz, new TransactionInterceptor(executor));
    }

    /**
     * 转换字节码方式实现声明式事务
     * 必须要在被转换的类加载之前调用，不支持热加载
     *
     * @param classNamePrefix 全类名的前缀，一般传包名就行
     */
    public synchronized static void transform(String classNamePrefix) {
        if (classNamePrefix == null || classNamePrefix.trim().equals("" )) {
            throw new IllegalArgumentException("类名前缀不能为空" );
        }

        MethodDelegation methodDelegation = MethodDelegation.to(TransactionDelegation.class);

        Instrumentation instrumentation = ByteBuddyAgent.install();

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module) ->
                builder.method(ElementMatchers.isAnnotatedWith(Transactional.class)).intercept(methodDelegation);

        new AgentBuilder.Default()
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .type(ElementMatchers.nameStartsWith(classNamePrefix))
                .transform(transformer)
                .installOn(instrumentation);

    }

    public static void setSlowTimeThreshold(int slowTimeThreshold) {
        Transaction.slowTimeThreshold = Math.max(0, slowTimeThreshold);
    }

    public static void setConflictThreshold(int conflictThreshold) {
        Transaction.conflictThreshold = Math.max(0, conflictThreshold);
    }

    public static void setPrintCountInterval(int printCountInterval) {
        Transaction.printCountInterval = Math.max(30, printCountInterval);
    }


}
