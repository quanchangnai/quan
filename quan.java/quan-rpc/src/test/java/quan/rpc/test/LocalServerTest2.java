package quan.rpc.test;

import quan.rpc.LocalServer;
import quan.rpc.NettyLocalServer;
import quan.rpc.ObjectReader;

/**
 * @author quanchangnai
 */
public class LocalServerTest2 {

    public static void main(String[] args) {
        ObjectReader.transferableFactory = new TransferableFactory();
        LocalServer localServer = new NettyLocalServer(2, "127.0.0.1", 9999, 5);
        localServer.addService(new RoleService(1));
        localServer.addRemote(1, "127.0.0.1", 8888);
        localServer.start();

    }

}
