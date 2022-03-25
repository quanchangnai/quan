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
    private int callId;

    /**
     * 调用的来源服务器ID
     */
    private int originServer;

    /**
     * 调用的来源线程ID，发起调用请求和处理返回结果要在同一个线程里
     */
    private int originThread;

    /**
     * 返回的结果
     */
    private Object result;

    public Response(int callId,int originServer, int originThread, Object result) {
        this.callId = callId;
        this.originServer = originServer;
        this.originThread = originThread;
        this.result = result;
    }

    public int getCallId() {
        return callId;
    }

    /**
     * @see #originServer
     */
    public int getOriginServer() {
        return originServer;
    }

    /**
     * @see #originThread
     */
    public int getOriginThread() {
        return originThread;
    }

    /**
     * @see #result
     */
    public Object getResult() {
        return result;
    }
}
