package quan.database.mongo;

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
public class ReadDelegation {

    private static Map<OperationExecutor, MongoClient> cache = new HashMap<>();

    @RuntimeType
    public static Object delegate(@This OperationExecutor executor, @SuperCall Callable<?> callable, @Argument(0) ReadOperation operation) throws Exception {
        if (!(operation instanceof FindOperation)) {
            return callable.call();
        }

        FindOperation findOperation = (FindOperation) operation;
        BatchCursor batchCursor = (BatchCursor) callable.call();
        return new Cursor(getMongoClient(executor), findOperation.getNamespace(), batchCursor);
    }

    private static MongoClient getMongoClient(OperationExecutor operationExecutor) throws Exception {
        MongoClient mongoClient = cache.get(operationExecutor);
        if (mongoClient != null) {
            return mongoClient;
        }

        Field field1 = operationExecutor.getClass().getDeclaredField("this$0");
        field1.setAccessible(true);
        MongoClientDelegate mongoClientDelegate = (MongoClientDelegate) field1.get(operationExecutor);

        Field field2 = mongoClientDelegate.getClass().getDeclaredField("originator");
        field2.setAccessible(true);
        mongoClient = (MongoClient) field2.get(mongoClientDelegate);

        cache.put(operationExecutor, mongoClient);

        return mongoClient;
    }

}
