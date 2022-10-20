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
     * 重连间隔时间(ms)
     */
    private int reconnectInterval = 5000;

    private int pingPongInterval = 5000;

    public final LocalServer getLocalServer() {
        return localServer;
    }

    public void setReconnectInterval(int reconnectInterval) {
        if (reconnectInterval < 1000) {
            throw new IllegalArgumentException("参数不能小于1000");
        }
        this.reconnectInterval = reconnectInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public int getPingPongInterval() {
        return pingPongInterval;
    }

    public void setPingPongInterval(int pingPongInterval) {
        if (pingPongInterval < 1000) {
            throw new IllegalArgumentException("参数不能小于1000");
        }
        this.pingPongInterval = pingPongInterval;
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
