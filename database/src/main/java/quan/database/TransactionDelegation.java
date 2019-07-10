package quan.database;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.tuple.One;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/**
 * 声明式事务代理
 */
public class TransactionDelegation {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDelegation.class);

    /**
     * 事务异步执行的线程池
     */
    static Executor executor;

    @RuntimeType
    public static Object delegate(@SuperCall Callable<?> callable, @Origin Method originMethod) {
        //被代理的方法的返回结果，同步调用一定能正确返回，异步调用结果会丢失
        One<Object> delegateResult = new One<>();

        Task task = () -> {
            try {
                Object callResult = callable.call();
                delegateResult.setOne(callResult);
                //返回false代表事务执行失败，其他值都表示事务执行成功
                if (callResult instanceof Boolean) {
                    return (boolean) callResult;
                }
                return true;
            } catch (Exception e) {
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

        if (delegateResult.getOne() != null) {
            return delegateResult.getOne();
        } else {
            return asyncCallDefaultResult(originMethod.getReturnType());
        }

    }

    /**
     * 异步调用无法返回实际结果，给个默认值，防止基本类型报空指针异常
     *
     * @param returnType
     * @return
     */
    private static Object asyncCallDefaultResult(Class<?> returnType) {
        if (returnType == boolean.class || returnType == Boolean.class) {
            return true;
        }
        if (returnType == byte.class || returnType == Byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class || returnType == Short.class) {
            return (short) 0;
        }
        if (returnType == int.class || returnType == Integer.class) {
            return 0;
        }
        if (returnType == long.class || returnType == Long.class) {
            return 0L;
        }
        if (returnType == float.class || returnType == Float.class) {
            return 0F;
        }
        if (returnType == double.class || returnType == Double.class) {
            return 0D;
        }
        return null;
    }

}
