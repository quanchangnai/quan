package quan.rpc.msg;

import quan.rpc.ObjectReader;
import quan.rpc.ObjectWriter;
import quan.rpc.Transferable;

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

    public Response(long callId, Object result) {
        this.callId = callId;
        this.result = result;
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

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(callId);
        writer.write(result);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        callId = reader.read();
        result = reader.read();
    }

}
