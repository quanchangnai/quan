package quan.rpc.test;

import quan.rpc.LocalServer;
import quan.rpc.NettyConnector;

/**
 * @author quanchangnai
 */
public class RpcTestNetty {

    public static void main(String[] args) {
        NettyConnector nettyConnector = new NettyConnector("127.0.0.1", 9999);
        LocalServer localServer = new LocalServer(2, 5, nettyConnector);
        nettyConnector.addRemote(1, "127.0.0.1", 8888);
        localServer.addService(new TestService2(2));
        localServer.start();
    }

}
