package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

/**
 * 调用请求协议
 *
 * @author quanchangnai
 */
public class Request extends Protocol {

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

    protected Request() {
    }

    public Request(Object serviceId, int methodId, Object... params) {
        this.serviceId = serviceId;
        this.methodId = methodId;
        this.params = params;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
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

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(callId);
        writer.write(serviceId);
        writer.write(methodId);
        writer.write(params);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        this.callId = reader.read();
        this.serviceId = reader.read();
        this.methodId = reader.read();
        this.params = reader.read();
    }

}
