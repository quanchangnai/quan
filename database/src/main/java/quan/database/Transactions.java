package quan.database;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
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
     * 创建子类代理对象方式实现声明式事务，支持父类热加载
     *
     * @param superclass 父类型
     * @param executor   不为空时在线程池中异步执行（最外层原始方法返回值将会丢失），为空时在被转换方法调用的当前线程同步执行
     * @param <T>
     * @return
     * @throws Exception
     */
    public synchronized static <T> T subclass(Class<T> superclass, Executor executor) {
        Objects.requireNonNull(superclass);

        if (executor != null) {
            TransactionDelegation.executor = executor;
        } else {
            TransactionDelegation.executor = null;
        }
        MethodDelegation methodDelegation = MethodDelegation.to(TransactionDelegation.class);

        ElementMatcher.Junction<MethodDescription> methodMatcher = ElementMatchers.isAnnotatedWith(Transactional.class);

        Class<? extends T> subclass = new ByteBuddy()
                .subclass(superclass)
                .method(methodMatcher)
                .intercept(methodDelegation)
                .make()
                .load(superclass.getClassLoader())
                .getLoaded();

        try {
            return subclass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }

    }

    public static <T> T subclass(Class<T> superclass) throws Exception {
        return subclass(superclass, null);
    }

    /**
     * 转换字节码方式实现声明式事务
     * 必须要在被转换的类加载之前调用，不支持热加载
     *
     * @param classNamePrefix 全类名的前缀，一般传包名就行
     * @param executor        不为空时在线程池中异步执行（最外层原始方法返回值将会丢失），为空时在被转换方法调用的当前线程同步执行
     */
    public synchronized static void transform(String classNamePrefix, Executor executor) {
        if (classNamePrefix == null || classNamePrefix.trim().equals("")) {
            throw new IllegalArgumentException("类名前缀不能为空");
        }
        if (executor != null) {
            TransactionDelegation.executor = executor;
        } else {
            TransactionDelegation.executor = null;
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

    public synchronized static void transform(String classNamePrefix) {
        transform(classNamePrefix, null);
    }


}
