package quan.data.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.OperationExecutor;
import com.mongodb.internal.operation.BatchCursor;
import com.mongodb.internal.operation.FindOperation;
import com.mongodb.internal.operation.ReadOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import quan.data.Transaction;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Aspect
public class OperationAspect {

    //一般情况只会创建一个MongoClient，这里兼容一下可能有多个数据源的情况
    private static Map<OperationExecutor, MongoClient> clients = new ConcurrentHashMap<>();

    @Pointcut("execution(* com.mongodb.client.internal.MongoClientDelegate.DelegateOperationExecutor.execute(..))")
    private void execute() {
    }

    @SuppressWarnings("rawtypes")
    @Around("execute() && args(com.mongodb.internal.operation.ReadOperation,..,com.mongodb.client.ClientSession)")
    public Object aroundRead(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Database.validateThread && !OperationThread.isInside()) {
            throw new IllegalStateException("只能在数据库线程里读数据库");
        }

        ReadOperation operation = (ReadOperation) joinPoint.getArgs()[0];
        if (!(operation instanceof FindOperation)) {
            return joinPoint.proceed();
        }

        BatchCursor cursor = (BatchCursor) joinPoint.proceed();
        MongoClient client = getMongoClient((OperationExecutor) joinPoint.getThis());

        String databaseName = ((FindOperation) operation).getNamespace().getDatabaseName();
        Database database = Database.getDatabase(client, databaseName);

        return new Cursor(database, cursor);
    }

    @Before("execute() && args(com.mongodb.internal.operation.WriteOperation,..,com.mongodb.client.ClientSession)")
    public void beforeWrite() {
        if (Transaction.isInside()) {
            throw new IllegalStateException("不能在内存事务中写数据库");
        }
        if (Database.validateThread && !OperationThread.isInside()) {
            throw new IllegalStateException("只能在数据库线程里写数据库");
        }
    }

    @Before("execution(* com.mongodb.client.internal.MongoClientImpl.close())")
    public void beforeClose(JoinPoint joinPoint) {
        MongoClient client = (MongoClient) joinPoint.getThis();
        List<ExecutorService> clientExecutors = Database.clientsExecutors.remove(client);
        if (clientExecutors != null) {
            //关闭线程池
            clientExecutors.forEach(ExecutorService::shutdown);
        }
    }

    private static MongoClient getMongoClient(OperationExecutor executor) throws Exception {
        MongoClient client = clients.get(executor);
        if (client != null) {
            return client;
        }

        Field field1 = executor.getClass().getDeclaredField("this$0");
        field1.setAccessible(true);
        Object mongoClientDelegate = field1.get(executor);

        Field field2 = mongoClientDelegate.getClass().getDeclaredField("originator");
        field2.setAccessible(true);
        client = (MongoClient) field2.get(mongoClientDelegate);

        clients.put(executor, client);

        return client;
    }

}
