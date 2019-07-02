package quan.database;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;


public class TransactionDelegation {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDelegation.class);

    volatile static Executor executor;

    @RuntimeType
    public static void delegate(@SuperCall Callable<?> callable) {
        Task task = () -> {
            try {
                Object result = callable.call();
                if (result instanceof Boolean) {
                    return (boolean) result;
                }
                return true;
            } catch (Exception e) {
                logger.error("", e);
                return false;
            }
        };

        if (executor != null) {
            executor.execute(task);
        } else {
            Transaction.execute(task);
        }
    }

}
