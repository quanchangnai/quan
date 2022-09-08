package quan.rpc;

import quan.rpc.protocol.Protocol;

import java.util.Set;

/**
 * 网络连接器，用于连接远程服务器和收发数据
 *
 * @author quanchangnai
 */
public abstract class Connector {

    protected LocalServer localServer;

    protected void start() {
    }

    protected void stop() {
    }

    protected void update() {
    }

    public abstract Set<Integer> getRemoteIds();

    protected abstract void sendProtocol(int remoteId, Protocol protocol);

}
