package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.msg.Handshake;

/**
 * @author quanchangnai
 */
public abstract class RemoteServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private int id;

    private String ip;

    private int port;

    private int reconnectTime = 5;

    private LocalServer localServer;

    protected RemoteServer(int id, String ip, int port) {
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

    protected abstract void sendMsg(Object msg);

    protected void handshake() {
        Handshake handshake = new Handshake(localServer.getId(), localServer.getIp(), localServer.getPort());
        sendMsg(handshake);
    }

}
