package quan.rpc;

public abstract class Proxy {

    /**
     * 目标服务器ID
     */
    private int serverId = -1;

    /**
     * 目标服务ID
     */
    private Object serviceId;

    public Proxy(int serverId, Object serviceId) {
        if (serverId < 0) {
            throw new IllegalArgumentException("目标服务器ID不能小于0");
        }
        this.serverId = serverId;
        this.serviceId = serviceId;
    }

    public Proxy(Object serviceId) {
        this.serviceId = serviceId;
    }

    protected abstract String _getServiceName$();

    //方法名加特殊字符，避免和服务方法同名
    protected <R> Promise<R> _sendRequest$(String signature, int securityModifier, int methodId, Object... params) {
        Worker worker = Worker.current();
        if (worker == null) {
            throw new IllegalStateException("当前所处线程不合法");
        }
        if (serverId < 0) {
            serverId = worker.resolveTargetServerId(_getServiceName$());
        }
        return worker.sendRequest(serverId, serviceId, signature, securityModifier, methodId, params);
    }

}
