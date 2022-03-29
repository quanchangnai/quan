package quan.rpc;

/**
 * RPC调用响应
 *
 * @author quanchangnai
 */
public class Response {

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
}
