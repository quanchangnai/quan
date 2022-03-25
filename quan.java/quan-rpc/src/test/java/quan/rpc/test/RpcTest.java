package quan.rpc.test;

import quan.rpc.RpcServer;

/**
 * @author quanchangnai
 */
public class RpcTest {

    public static void main(String[] args)  throws Exception{
        RpcServer rpcServer = new RpcServer(1, 5);
        rpcServer.addService(new TestService(1));
        rpcServer.addService(new TestService(2));
        rpcServer.start();

    }

}
