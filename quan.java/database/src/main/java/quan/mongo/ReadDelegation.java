package quan.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientDelegate;
import com.mongodb.client.internal.OperationExecutor;
import com.mongodb.operation.BatchCursor;
import com.mongodb.operation.FindOperation;
import com.mongodb.operation.ReadOperation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by quanchangnai on 2020/4/22.
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public class ReadDelegation {

    //一般情况只会创建一个MongoClient，这里兼容一下多个的情况
    private static Map<OperationExecutor, MongoClient> clients = new HashMap<>();

    @RuntimeType
    public static Object delegate(@This OperationExecutor executor, @SuperCall Callable<?> callable, @Argument(0) ReadOperation operation) throws Exception {
        if (!(operation instanceof FindOperation)) {
            return callable.call();
        }

        FindOperation findOperation = (FindOperation) operation;
        BatchCursor cursor = (BatchCursor) callable.call();

        return new Cursor(getMongoClient(executor), findOperation.getNamespace(), cursor);
    }

    private static MongoClient getMongoClient(OperationExecutor executor) throws Exception {
        MongoClient client = clients.get(executor);
        if (client != null) {
            return client;
        }

        Field field1 = executor.getClass().getDeclaredField("this$0");
        field1.setAccessible(true);
        MongoClientDelegate mongoClientDelegate = (MongoClientDelegate) field1.get(executor);

        Field field2 = mongoClientDelegate.getClass().getDeclaredField("originator");
        field2.setAccessible(true);
        client = (MongoClient) field2.get(mongoClientDelegate);

        clients.put(executor, client);

        return client;
    }

}
