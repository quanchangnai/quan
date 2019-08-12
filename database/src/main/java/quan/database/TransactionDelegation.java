package quan.database;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 声明式事务代理
 */
public class TransactionDelegation {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDelegation.class);


    @RuntimeType
    public static Object delegate(@SuperCall Callable<?> callable, @Origin Method originMethod) {
        //被代理的方法的返回结果
        AtomicReference<Object> result = new AtomicReference<>();

        Task task = () -> {
            try {
                Object callResult = callable.call();
                result.set(callable.call());
                return result.get() instanceof Boolean ? (boolean) callResult : true;
            } catch (Exception e) {
                if (!(e instanceof Transaction.BreakdownException)) {
                    logger.error("", e);
                }
                return false;
            }
        };

        Transaction.execute(task);

        return result.get();

    }

}
