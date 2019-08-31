package quan.database;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;


/**
 * cglib声明式事务拦截器
 */
public class TransactionInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

    /**
     * 事务异步执行的线程池
     */
    private Executor executor;

    public TransactionInterceptor() {
    }

    public TransactionInterceptor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) {
        //被代理的方法的返回结果，同步调用一定能正确返回，异步调用结果会丢失
        AtomicReference<Object> result = new AtomicReference<>();

        Task task = () -> {
            try {
                Object superResult = methodProxy.invokeSuper(obj, args);
                result.set(superResult);
                //返回false代表事务执行失败，其他值都表示事务执行成功
                return !(superResult instanceof Boolean) || (boolean) superResult;
            } catch (Throwable e) {
                if (!(e instanceof Transaction.BreakdownException)) {
                    logger.error("", e);
                }
                return false;
            }
        };

        if (executor != null) {
            executor.execute(task);
        } else {
            Transaction.execute(task);
        }

        if (result.get() == null) {
            //异步调用无法返回实际结果，给个默认值，防止基本类型报空指针异常
            return defaultValue(method.getReturnType());
        } else {
            return result.get();
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return true;
        }
        if (type == byte.class || type == Byte.class) {
            return (byte) 0;
        }
        if (type == short.class || type == Short.class) {
            return (short) 0;
        }
        if (type == int.class || type == Integer.class) {
            return 0;
        }
        if (type == long.class || type == Long.class) {
            return 0L;
        }
        if (type == float.class || type == Float.class) {
            return 0F;
        }
        if (type == double.class || type == Double.class) {
            return 0D;
        }
        return null;
    }

}
