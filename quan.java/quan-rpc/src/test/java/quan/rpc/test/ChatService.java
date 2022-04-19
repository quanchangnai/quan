package quan.rpc.test;

import quan.rpc.Endpoint;
import quan.rpc.Service;
import quan.rpc.SingletonService;

/**
 * @author quanchangnai
 */
@SingletonService(id = "chat")
public class ChatService extends Service {

    @Endpoint
    public void sendChatMsg(String msg) {

    }

}
