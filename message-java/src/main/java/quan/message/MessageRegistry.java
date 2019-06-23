package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息注册表
 * Created by quanchangnai on 2019/6/20.
 */
public class MessageRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, Message> messages = new HashMap<>();

    public void registerMessage(Message message) {
        if (messages.containsKey(message.getId())) {
            throw new RuntimeException("消息ID重复:" + message.getId());
        }
        messages.put(message.getId(), message);
    }

    public Message createMessage(int msgId) {
        Message message = messages.get(msgId);
        if (message == null) {
            return null;
        }
        return message.create();
    }

}
