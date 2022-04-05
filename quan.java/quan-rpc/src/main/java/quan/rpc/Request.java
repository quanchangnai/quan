package quan.rpc;

/**
 * RPC调用请求
 *
 * @author quanchangnai
 */
public class Request {

    /**
     * 调用ID
     */
    private long callId;

    /**
     * 目标服务ID
     */
    private Object serviceId;

    /**
     * 目标方法ID
     */
    private int methodId;

    /**
     * 目标方法参数
     */
    private Object[] params;

    public Request(Object serviceId, int methodId, Object... params) {
        this.serviceId = serviceId;
        this.methodId = methodId;
        this.params = params;
    }

    public long getCallId() {
        return callId;
    }

    protected void setCallId(long callId) {
        this.callId = callId;
    }

    /**
     * @see #serviceId
     */
    public Object getServiceId() {
        return serviceId;
    }

    /**
     * @see #methodId
     */
    public int getMethodId() {
        return methodId;
    }

    /**
     * @see #params
     */
    public Object[] getParams() {
        return params;
    }

}
