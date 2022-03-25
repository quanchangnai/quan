package quan.rpc;

import java.lang.reflect.Method;

/**
 * @author quanchangnai
 */
public abstract class Service {

    /**
     * 服务所属的线程
     */
    RpcThread thread;

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

    public Object call(String methodName, Object... params) {
        Class<?>[] parameterTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterTypes[i] = params[i].getClass();
        }
        try {
            Method method = getClass().getMethod(methodName, parameterTypes);
            return method.invoke(this, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void update() {

    }

}
