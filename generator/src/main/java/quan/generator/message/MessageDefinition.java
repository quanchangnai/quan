package quan.generator.message;

import quan.generator.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private static Map<String, MessageDefinition> messages = new HashMap<>();

    private String id;

    @Override
    public int getDefinitionType() {
        return 3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("消息ID必须是整数", e);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (messages.containsKey(id)) {
            throw new RuntimeException(messages.get(id).getName() + "和" + this.getName() + "消息ID重复:" + id);
        }
        messages.put(id, this);
    }
}
