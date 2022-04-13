package quan.rpc.msg;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;
import quan.rpc.serialize.Transferable;

/**
 * RPC调用响应
 *
 * @author quanchangnai
 */
public class Response implements Transferable {

    /**
     * 调用ID
     */
    private long callId;

    /**
     * 返回的结果
     */
    private Object result;

    private boolean error;

    public Response() {
    }

    public Response(long callId, Object result, boolean error) {
        this.callId = callId;
        this.result = result;
        this.error = error;
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

    public boolean isError() {
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
