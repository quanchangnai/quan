package quan.transaction;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class TransactionInterceptor {

    @RuntimeType
    public static void intercept(@SuperCall Runnable superCall) throws Exception {
        Transaction.execute(superCall);
    }

}