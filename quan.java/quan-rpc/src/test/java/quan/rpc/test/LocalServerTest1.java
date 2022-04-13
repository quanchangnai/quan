package quan.rpc.test;

import quan.rpc.LocalServer;
import quan.rpc.NettyLocalServer;
import quan.rpc.ObjectReader;

/**
 * @author quanchangnai
 */
public class LocalServerTest1 {

    public static void main(String[] args) {
        ObjectReader.transferableFactory = new TransferableFactory();
        LocalServer localServer = new NettyLocalServer(1, "127.0.0.1", 8888, 5);
        localServer.addService(new TestService(1));
        localServer.addService(new TestService(2));
        localServer.start();
    }

}
