package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.utils.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 消息注册表
 * Created by quanchangnai on 2019/6/20.
 */
public class MessageRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<Integer, Message> messages = new HashMap<>();

    public MessageRegistry() {
    }

    public MessageRegistry(String messagePackage) {
        register(messagePackage);
    }

    public void register(Message message) {
        Objects.requireNonNull(message, "参数[message]不能为空");
        if (messages.put(message.getId(), message) != null) {
            throw new IllegalArgumentException("消息ID[" + message.getId() + "]不能重复");
        }
    }

    public void register(String messagePackage) {
        Set<Class<?>> messageClasses = ClassUtils.loadClasses(messagePackage, Message.class);
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
        Message message = messages.get(msgId);
        if (message == null) {
            return null;
        }
        return message.create();
    }

}
