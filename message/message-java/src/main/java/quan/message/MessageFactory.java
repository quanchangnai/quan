package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 消息工厂
 * Created by quanchangnai on 2019/6/20.
 */
public class MessageFactory {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<Integer, Message> prototypes = new HashMap<>();

    public void register(Message message) {
        if (prototypes.containsKey(message.getId())) {
            throw new RuntimeException("消息ID重复:" + message.getId());
        }
        prototypes.put(message.getId(), message);
    }

    public void autoRegister(String packageName) {
        Set<Class<?>> messageClasses = ClassUtils.loadClasses(packageName, Message.class);
        for (Class<?> messageClass : messageClasses) {
            try {
                Message message = (Message) messageClass.getDeclaredConstructor().newInstance();
                register(message);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public Message create(int msgId) {
        Message message = prototypes.get(msgId);
        if (message == null) {
            return null;
        }
        return message.create();
    }

}
