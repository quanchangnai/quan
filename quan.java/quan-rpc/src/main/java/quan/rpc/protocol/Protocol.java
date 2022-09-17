package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;
import quan.rpc.serialize.Transferable;
import quan.rpc.serialize.TransferableRegistry;

/**
 * RPC协议
 */
public abstract class Protocol implements Transferable {

    /**
     * 来源服务器ID
     */
    private int serverId;

    private static volatile TransferableRegistry registry;

    public Protocol() {
    }

    public Protocol(int serverId) {
        this.serverId = serverId;
    }

    public static TransferableRegistry getRegistry() {
        if (registry == null) {
            synchronized (Protocol.class) {
                if (registry == null) {
                    registry = new TransferableRegistry();
                    registry.register(1, Handshake.class, Handshake::new);
                    registry.register(2, PingPong.class, PingPong::new);
                    registry.register(3, Request.class, Request::new);
                    registry.register(4, Response.class, Response::new);
                }
            }
        }
        return registry;
    }

    /**
     * @see #serverId
     */
    public int getServerId() {
        return serverId;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(serverId);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        serverId = reader.read();
    }

}
