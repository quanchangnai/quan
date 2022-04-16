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

    private String error;

    protected Response() {
    }

    public Response(long callId, Object result, String error) {
        this.callId = callId;
        this.result = result;
        this.error = error;
    }

    @Override
    public int getType() {
        return 4;
    }

    public long getCallId() {
        return callId;
    }

    /**
     * @see #result
     */
    public Object getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(callId);
        writer.write(result);
        writer.write(error);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        callId = reader.read();
        result = reader.read();
        error = reader.read();
    }

}
