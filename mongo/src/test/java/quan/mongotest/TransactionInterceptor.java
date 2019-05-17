package quan.mongotest;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.List;
import java.util.concurrent.Callable;

public class TransactionInterceptor {

    @RuntimeType
    public static Object log(@SuperCall Callable superCall)
            throws Exception {
        System.out.println("Calling database");
        try {

            return  superCall.call();
        } finally {
            System.out.println("Returned from database");
        }
    }

}