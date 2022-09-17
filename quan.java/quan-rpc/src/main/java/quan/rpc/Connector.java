package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.protocol.Protocol;

import java.util.Set;

/**
 * 网络连接器，用于连接远程服务器和收发数据
 *
 * @author quanchangnai
 */
public abstract class Connector {

    protected final static Logger logger = LoggerFactory.getLogger(Connector.class);

    protected LocalServer localServer;

    /**
     * 重连时间间隔(秒)
     */
    private int reconnectInterval = 5;

    public final LocalServer getLocalServer() {
        return localServer;
    }

    public void setReconnectInterval(int reconnectInterval) {
        if (reconnectInterval < 1) {
            throw new IllegalArgumentException("重连时间必需大于1秒");
        }
        this.reconnectInterval = reconnectInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    protected void start() {
    }

    protected void stop() {
    }

    protected void update() {
    }

    /**
     * 连接器管理的所有远程服务器ID
     */
    public abstract Set<Integer> getRemoteIds();

    /**
     * 远程服务器是否已激活
     */
    public abstract boolean isRemoteActivated(int remoteId);

    protected abstract void sendProtocol(int remoteId, Protocol protocol);

}
