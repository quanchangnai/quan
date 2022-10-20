package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

/**
 * 调用响应协议
 *
 * @author quanchangnai
 */
public class Response extends Protocol {

    /**
     * 调用ID
     */
    private long callId;

    /**
     * 返回的结果
     */
    private Object result;

    private String exception;

    protected Response() {
    }

    public Response(int serverId, long callId, Object result, String exception) {
        super(serverId);
        this.callId = callId;
        this.result = result;
        this.exception = exception;
    }

    public long getCallId() {
        return callId;
    }

    public Object getResult() {
        return result;
    }

    public String getException() {
        return exception;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        super.transferTo(writer);
        writer.write(callId);
        writer.write(result);
        writer.write(exception);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        super.transferFrom(reader);
        callId = reader.read();
        result = reader.read();
        exception = reader.read();
    }

    @Override
    public String toString() {
        return "Response{" +
                "callId=" + callId +
                ", result=" + result +
                ", exception='" + exception + '\'' +
                '}';
    }

}
