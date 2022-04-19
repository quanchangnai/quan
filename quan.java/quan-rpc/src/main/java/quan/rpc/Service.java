package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务
 *
 * @author quanchangnai
 */
public abstract class Service {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Map<Class<? extends Service>, Service> singletonServices = new ConcurrentHashMap<>();

    //单例服务ID
    private final Object id;

    /**
     * 服务所属的工作线程
     */
    Worker worker;

    private Caller caller;

    {
        id = checkSingleton();
    }

    /**
     * 服务ID，在同一个{@link LocalServer}内必需保证唯一性，非单例服务应该覆盖此方法
     */
    public Object getId() {
        if (id != null) {
            return id;
        } else {
            throw new IllegalStateException("服务ID不存在");
        }
    }

    private Object checkSingleton() {
        Class<? extends Service> clazz = getClass();
        SingletonService singletonService = clazz.getAnnotation(SingletonService.class);
        if (singletonService == null) {
            return null;
        }
        if (singletonServices.putIfAbsent(clazz, this) != null) {
            throw new IllegalStateException("单例服务不能重复构造");
        }
        return singletonService.id();
    }

    /**
     * @see #worker
     */
    public final Worker getWorker() {
        return worker;
    }

    final Object call(int methodId, Object... params) throws Exception {
        if (caller == null) {
            Class<?> callerClass = Class.forName(getClass().getName() + "Caller");
            this.caller = (Caller) callerClass.getField("instance").get(callerClass);
        }
        return caller.call(this, methodId, params);
    }

    /**
     * 在服务当前所属的工作线程中执行任务
     */
    public final void execute(Runnable task) {
        worker.execute(task);
    }

    /**
     * 刷帧
     */
    protected void update() {
    }

    public final <R> DelayedResult<R> newDelayedResult() {
        return worker.newDelayedResult();
    }

}
