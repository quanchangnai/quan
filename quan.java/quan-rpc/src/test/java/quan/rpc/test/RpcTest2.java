package quan.rpc.test;

import quan.rpc.NettyConnector;
import quan.rpc.LocalServer;

/**
 * @author quanchangnai
 */
public class RpcTest2 {

    public static void main(String[] args) {
        NettyConnector nettyConnector = new NettyConnector("127.0.0.1", 9999);
        LocalServer localServer = new LocalServer(2, 5, nettyConnector);
        nettyConnector.addRemote(1, "127.0.0.1", 8888);
        localServer.addService(new TestService2(2));
        localServer.start();

    }

}
