package quan.rpc;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.protocol.Handshake;
import quan.rpc.protocol.PingPong;
import quan.rpc.protocol.Protocol;

/**
 * @author quanchangnai
 */
public abstract class RemoteServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private int id;

    private String ip;

    private int port;

    private int reconnectTime = 5;

    protected LocalServer localServer;

    private boolean activated;

    private long lastSendPingPongTime;

    private long lastReceivedPingPongTime = System.currentTimeMillis();

    private long lastReportConnectionSuspendedTime;

    protected RemoteServer(int id, String ip, int port) {
        Validate.isTrue(id > 0, "服务器ID必须是正整数");
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public final int getId() {
        return id;
    }

    public final String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final int getReconnectTime() {
        return reconnectTime;
    }

    final void setLocalServer(LocalServer localServer) {
        this.localServer = localServer;
        if (localServer.getReconnectTime() > 0) {
            this.reconnectTime = localServer.getReconnectTime();
        }
    }

    protected abstract void start();

    protected abstract void stop();

    protected abstract void send(Protocol protocol);

    protected void setActivated(boolean activated) {
        this.activated = activated;
        if (activated) {
            Handshake handshake = new Handshake(localServer.getId(), localServer.getIp(), localServer.getPort());
            send(handshake);
        }
    }

    public boolean isActivated() {
        return activated;
    }

    protected void update() {
        checkConnectionSuspended();
        sendPingPong();
    }

    private void checkConnectionSuspended() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReceivedPingPongTime > 30000 && currentTime - lastReportConnectionSuspendedTime > 60000) {
            logger.error("远程服务器[{}]的连接可能已经进入假死状态了", localServer.getId());
            lastReportConnectionSuspendedTime = currentTime;
        }
    }

    protected void sendPingPong() {
        if (!activated) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (lastSendPingPongTime + 5000 < currentTime) {
            send(new PingPong(currentTime));
            lastSendPingPongTime = currentTime;
        }
    }

    protected void handlePingPong(PingPong pingPong) {
        lastReceivedPingPongTime = System.currentTimeMillis();
        logger.debug("远程服务器[{}]的延迟时间为：{}ms", this.id, lastReceivedPingPongTime - pingPong.getTime());
    }

}
