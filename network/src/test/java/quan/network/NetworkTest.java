package quan.network;

import quan.network.message.MessageRegistry;
import quan.network.message.role.SRoleLogin;

/**
 * Created by quanchangnai on 2017/7/3.
 */
public class NetworkTest {

    public static void main(String[] args) throws Exception {

    }


    public static MessageRegistry messageRegistry = new MessageRegistry();

    static {
        messageRegistry.registerMessage(new SRoleLogin());
    }
}
