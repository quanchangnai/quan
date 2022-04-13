package quan.rpc.msg;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;
import quan.rpc.serialize.Transferable;

/**
 * @author quanchangnai
 */
public class Handshake implements Transferable {

    private int serverId;

    private String serverIp;

    private int serverPort;

    public int getServerId() {
        return serverId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public Handshake() {
    }

    public Handshake(int serverId, String serverIp, int serverPort) {
        this.serverId = serverId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(serverId);
        writer.write(serverIp);
        writer.write(serverPort);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        serverId = reader.read();
        serverIp = reader.read();
        serverPort = reader.read();
    }

}
