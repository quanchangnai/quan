package quan.network;

import quan.network.message.MessageRegistry;

/**
 * Created by quanchangnai on 2017/7/3.
 */
public class NetworkTest {

    public static void main(String[] args) {

    }


    public static MessageRegistry messageRegistry = new MessageRegistry();

    static {
        messageRegistry.registerMessage(new SRoleLogin());
    }
}
