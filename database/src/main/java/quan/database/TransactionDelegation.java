package quan.database;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.tuple.One;

import java.util.concurrent.Callable;


/**
 * 声明式事务代理
 */
public class TransactionDelegation {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDelegation.class);

    volatile static Executor executor;

    @RuntimeType
    public static Object delegate(@SuperCall Callable<?> callable) {
        //被代理的方法的返回结果，同步调用一定能返回，异步调用结果会丢失
        One<Object> delegateResult = new One<>();

        Task task = () -> {
            try {
                Object callableResult = callable.call();
                delegateResult.setOne(callableResult);
                if (callableResult instanceof Boolean) {
                    return (boolean) callableResult;
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

        return delegateResult.getOne();
    }

}
