package quan.rpc.protocol;

import quan.rpc.serialize.Transferable;

/**
 * RPC协议
 */
public abstract class Protocol implements Transferable {

    /**
     * 协议类型
     */
    public abstract int getType();

    public static Protocol create(int type) {
        switch (type) {
            case 1:
                return new Handshake();
            case 2:
                return new PingPong();
            case 3:
                return new Request();
            case 4:
                return new Response();
            default:
                throw new IllegalArgumentException("不支持的协议类型：" + type);
        }
    }

}
