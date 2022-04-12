package quan.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

/**
 * @author quanchangnai
 */
public class RemoteServer {

    protected static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);

    private int id;

    private String ip;

    private int port;

    public RemoteServer(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void start() {

    }

    public void stop() {

    }

    protected void sendRequest(Request request) {
    }

    protected void sendResponse(Response response) {
    }

}
