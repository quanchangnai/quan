package quan.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.util.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 消息注册表
 */
public class MessageRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<Integer, Message> id2Messages = new HashMap<>();

    protected Map<Class<? extends Message>, Message> class2Messages = new HashMap<>();

    public MessageRegistry() {
    }

    public MessageRegistry(String messagePackage) {
        register(messagePackage);
    }

    public void register(Message message) {
        Objects.requireNonNull(message, "参数[message]不能为空");
        if (id2Messages.putIfAbsent(message.getId(), message) != null) {
            throw new IllegalArgumentException("消息ID[" + message.getId() + "]不能重复");
        }
        class2Messages.put(message.getClass(), message);
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
        Message message = id2Messages.get(msgId);
        if (message == null) {
            return null;
        }
        return message.create();
    }

    public Message create(Class<? extends Message> msgClass) {
        Message message = class2Messages.get(msgClass);
        if (message == null) {
            return null;
        }
        return message.create();
    }

}
