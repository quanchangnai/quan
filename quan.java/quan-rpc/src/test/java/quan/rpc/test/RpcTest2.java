package quan.rpc.test;

import quan.rpc.LocalServer;
import quan.rpc.NettyLocalServer;

/**
 * @author quanchangnai
 */
public class RpcTest2 {

    public static void main(String[] args) {
        LocalServer localServer = new NettyLocalServer(2, "127.0.0.1", 9999, 5);
        localServer.addService(new TestService2(2));
        localServer.addRemote(1, "127.0.0.1", 8888);
        localServer.start();

    }

}
