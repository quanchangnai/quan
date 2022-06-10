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

    private int reconnectInterval = 5;

    protected LocalServer localServer;

    private boolean connected;

    private long lastSendPingPongTime;

    private long lastHandlePingPongTime = System.currentTimeMillis();

    private long lastReportSuspendedTime;

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

    public final int getReconnectInterval() {
        return reconnectInterval;
    }

    final void setLocalServer(LocalServer localServer) {
        this.localServer = localServer;
        if (localServer.getReconnectInterval() > 0) {
            this.reconnectInterval = localServer.getReconnectInterval();
        }
    }

    protected abstract void start();

    protected abstract void stop();

    protected abstract void send(Protocol protocol);

    protected void setConnected(boolean connected) {
        this.connected = connected;
        if (connected) {
            Handshake handshake = new Handshake(localServer.getId(), localServer.getIp(), localServer.getPort());
            send(handshake);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    protected void update() {
        checkSuspended();
        sendPingPong();
    }

    private void checkSuspended() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHandlePingPongTime > 30000 && currentTime - lastReportSuspendedTime > 60000) {
            logger.error("远程服务器[{}]的连接可能已经进入假死状态了", localServer.getId());
            lastReportSuspendedTime = currentTime;
        }
    }

    protected void sendPingPong() {
        if (!connected) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (lastSendPingPongTime + 5000 < currentTime) {
            send(new PingPong(currentTime));
            lastSendPingPongTime = currentTime;
        }
    }

    protected void handlePingPong(PingPong pingPong) {
        lastHandlePingPongTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("远程服务器[{}]的延迟时间为：{}ms", this.id, lastHandlePingPongTime - pingPong.getTime());
        }
    }

}
