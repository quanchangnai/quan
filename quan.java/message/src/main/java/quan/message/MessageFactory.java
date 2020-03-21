package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 消息工厂
 * Created by quanchangnai on 2019/6/20.
 */
public class MessageFactory {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<Integer, Message> registry = new HashMap<>();

    public void register(Message message) {
        Objects.requireNonNull(message, "参数[message]不能为空");
        if (registry.containsKey(message.getId())) {
            throw new IllegalArgumentException("消息ID[" + message.getId() + "]不能重复");
        }
        registry.put(message.getId(), message);
    }

    public void register(String packageName) {
        Set<Class<?>> messageClasses = ClassUtils.loadClasses(packageName, Message.class);
        for (Class<?> messageClass : messageClasses) {
            try {
                if (Modifier.isAbstract(messageClass.getModifiers())) {
                    continue;
                }
                Message message = (Message) messageClass.getDeclaredConstructor().newInstance();
                register(message);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public Message create(int msgId) {
        Message message = registry.get(msgId);
        if (message == null) {
            return null;
        }
        return message.create();
    }

}
