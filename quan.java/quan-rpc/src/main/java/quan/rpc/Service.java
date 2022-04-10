package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
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
     * 服务ID，在同一个{@link RpcServer}内必需保证唯一性
     */
    public abstract Object getId();

    /**
     * @see #worker
     */
    public final Worker getWorker() {
        return worker;
    }

    final Object call(int methodId, Object... params) {
        if (caller == null) {
            try {
                Class<?> callerClass = Class.forName(getClass().getName() + "Caller");
                this.caller = (Caller) callerClass.getField("instance").get(callerClass);
            } catch (Exception e) {
                logger.error("", e);
                return null;
            }
        }

        return caller.call(this, methodId, params);
    }

    /**
     * 刷帧
     */
    protected void update() {
    }

}
