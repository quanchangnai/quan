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
     * 目标服务器
     */
    private int targetServer;

    /**
     * 目标服务ID
     */
    private Object service;

    /**
     * 目标方法名
     */
    private String method;

    /**
     * 目标方法参数
     */
    private Object[] params;

    public Request(int targetServer, Object service, String method, Object... params) {
        this.targetServer = targetServer;
        this.service = service;
        this.method = method;
        this.params = params;
    }

    public int getCallId() {
        return callId;
    }

    protected void setCallId(int callId) {
        this.callId = callId;
    }

    /**
     * @see #originServer
     */
    public int getOriginServer() {
        return originServer;
    }

    /**
     * @see #originServer
     */
    protected void setOriginServer(int originServer) {
        this.originServer = originServer;
    }

    /**
     * @see #originThread
     */
    protected void setOriginThread(int originThread) {
        this.originThread = originThread;
    }

    /**
     * @see #originThread
     */
    public int getOriginThread() {
        return originThread;
    }

    public int getTargetServer() {
        return targetServer;
    }

    /**
     * @see #service
     */
    public Object getService() {
        return service;
    }

    /**
     * @see #method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @see #params
     */
    public Object[] getParams() {
        return params;
    }

}
