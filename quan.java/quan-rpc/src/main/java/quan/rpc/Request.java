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
    private String methodId;

    /**
     * 目标方法参数
     */
    private Object[] methodParams;

    public Request(Object serviceId, String methodId, Object... methodParams) {
        this.serviceId = serviceId;
        this.methodId = methodId;
        this.methodParams = methodParams;
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
    public String getMethodId() {
        return methodId;
    }

    /**
     * @see #methodParams
     */
    public Object[] getMethodParams() {
        return methodParams;
    }

}
