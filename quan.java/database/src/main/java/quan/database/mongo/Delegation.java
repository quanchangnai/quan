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
public class Delegation {

    private static Map<OperationExecutor, MongoClient> cache = new HashMap<>();

    @RuntimeType
    public static Object delegate(@This OperationExecutor operationExecutor, @SuperCall Callable<?> callable, @Argument(0) ReadOperation readOperation) throws Exception {

        if (readOperation instanceof FindOperation) {
            FindOperation findOperation = (FindOperation) readOperation;
            BatchCursor batchCursor = (BatchCursor) callable.call();
            Cursor cursor = new Cursor(getMongoClient(operationExecutor), findOperation.getNamespace(), batchCursor);
            return cursor;
        }

        return callable.call();
    }

    private static MongoClient getMongoClient(OperationExecutor operationExecutor) throws Exception {
        MongoClient mongoClient = cache.get(operationExecutor);
        if (mongoClient != null) {
            return mongoClient;
        }
        Field this$0Field = operationExecutor.getClass().getDeclaredField("this$0");
        this$0Field.setAccessible(true);
        MongoClientDelegate mongoClientDelegate = (MongoClientDelegate) this$0Field.get(operationExecutor);

        Field originatorField = mongoClientDelegate.getClass().getDeclaredField("originator");
        originatorField.setAccessible(true);
        mongoClient = (MongoClient) originatorField.get(mongoClientDelegate);

        cache.put(operationExecutor, mongoClient);

        return mongoClient;
    }

}
