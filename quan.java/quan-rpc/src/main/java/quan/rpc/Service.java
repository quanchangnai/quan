package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务
 *
 * @author quanchangnai
 */
public abstract class Service {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务所属的工作线程
     */
    Worker worker;

    private Caller caller;

    /**
     * 服务ID，在同一个{@link LocalServer}内必需保证唯一性
     */
    public abstract Object getId();

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
