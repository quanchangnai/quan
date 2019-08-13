package quan.database;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

/**
 * 事务工具类
 * Created by quanchangnai on 2019/7/2.
 */
@SuppressWarnings({"unchecked"})
public class Transactions {

    private static final Logger logger = LoggerFactory.getLogger(Transactions.class);


    /**
     * 创建代理子类方式实现声明式事务，支持父类热加载
     *
     * @param clazz 目标类型
     */
    public synchronized static <T> Class<? extends T> subclass(Class<T> clazz) {
        Objects.requireNonNull(clazz, "目标类型不能为空" );
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


    private static <T> Enhancer createEnhancer(Class<T> clazz, Executor executor) {
        Objects.requireNonNull(clazz, "目标类型不能为空" );
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallbacks(new Callback[]{new TransactionInterceptor(executor), NoOp.INSTANCE});
        enhancer.setCallbackFilter(method -> method.isAnnotationPresent(Transactional.class) ? 0 : 1);
        return enhancer;
    }

    /**
     * 创建代理子类对象方式实现声明式事务
     *
     * @see #proxy(Class, Class[], Object[], Executor)
     */
    public static <T> T proxy(Class<T> clazz, Executor executor) {
        return (T) createEnhancer(clazz, executor).create();
    }

    /**
     * 创建子类代理对象方式实现声明式事务
     *
     * @see #proxy(Class, Class[], Object[], Executor)
     */
    public static <T> T proxy(Class<T> clazz) {
        return proxy(clazz, null);
    }

    /**
     * 创建代理子类对象方式实现声明式事务，支持父类热加载
     *
     * @param clazz         目标类型
     * @param argumentTypes 构造方法参数类型
     * @param arguments     构造方法参数
     * @param executor      异步线程池为空时将同步执行,同步执行并能返回正确结果，异步执行最外层原始方法返回值将会丢失
     */
    public static <T> T proxy(Class<T> clazz, Class[] argumentTypes, Object[] arguments, Executor executor) {
        return (T) createEnhancer(clazz, executor).create(argumentTypes, arguments);
    }

    /**
     * 创建子类代理对象方式实现声明式事务
     *
     * @see #proxy(Class, Class[], Object[], Executor)
     */
    public static <T> T proxy(Class<T> clazz, Class[] argumentTypes, Object[] arguments) {
        return (T) createEnhancer(clazz, null).create(argumentTypes, arguments);
    }

    /**
     * 转换字节码方式实现声明式事务，必须要在被转换的类加载之前调用，不支持热加载
     *
     * @param classNamePrefix 全类名的前缀，一般传包名就行
     */
    public synchronized static void transform(String classNamePrefix) {
        if (StringUtils.isBlank(classNamePrefix)) {
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

    /**
     * 设置慢事务时间阈值
     */
    public static void setSlowTimeThreshold(int slowTimeThreshold) {
        Transaction.slowTimeThreshold = Math.max(0, slowTimeThreshold);
    }

    /**
     * 设置事务中的逻辑执行冲突次数阈值(ms)
     */
    public static void setConflictThreshold(int conflictThreshold) {
        Transaction.conflictThreshold = Math.max(0, conflictThreshold);
    }

    /**
     * 打印统计信息间隔(秒)
     */
    public static void setPrintCountInterval(int printCountInterval) {
        Transaction.printCountInterval = Math.max(30, printCountInterval);
    }


}
