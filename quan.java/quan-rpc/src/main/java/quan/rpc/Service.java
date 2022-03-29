package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * @author quanchangnai
 */
public abstract class Service {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务所属的线程
     */
    RpcThread thread;

    private Caller caller;

    {
        initCaller();
    }

    /**
     * 服务ID，在同一个RPC服务器内必需保证唯一性
     */
    public abstract Object getId();

    /**
     * @see #thread
     */
    public RpcThread getThread() {
        return thread;
    }

    private void initCaller() {
        try {
            Class<?> callerClass = Class.forName(getClass().getName() + "Proxy$Caller");
            Constructor<?> constructor = callerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            this.caller = (Caller) constructor.newInstance();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public Object call(String methodId, Object... methodParams) {
        return caller.call(this, methodId, methodParams);
    }

    /**
     * 刷帧
     */
    protected void update() {

    }

}
