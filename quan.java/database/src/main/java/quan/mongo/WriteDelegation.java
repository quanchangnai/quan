package quan.mongo;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import quan.data.Transaction;

import java.util.concurrent.Callable;

/**
 * Created by quanchangnai on 2020/4/22.
 */
public class WriteDelegation {

    @RuntimeType
    public static Object delegate(@SuperCall Callable<?> callable) throws Exception {
        if (Transaction.isInside()) {
            throw new IllegalStateException("不能在内存事务中写数据库");
        }
        return callable.call();
    }

}
