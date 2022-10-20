package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

import java.util.Arrays;

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

    public Request(int serverId, Object serviceId, int methodId, Object... params) {
        super(serverId);
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

    public Object getServiceId() {
        return serviceId;
    }

    public int getMethodId() {
        return methodId;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        super.transferTo(writer);
        writer.write(callId);
        writer.write(serviceId);
        writer.write(methodId);
        writer.write(params);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        super.transferFrom(reader);
        this.callId = reader.read();
        this.serviceId = reader.read();
        this.methodId = reader.read();
        this.params = reader.read();
    }

    @Override
    public String toString() {
        return "Request{" +
                "callId=" + callId +
                ", serviceId=" + serviceId +
                ", methodId=" + methodId +
                ", params=" + Arrays.toString(params) +
                '}';
    }

}
