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
    }

    @Override
    public void validate() {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throwValidateError("消息ID必须是整数");
        }

        MessageDefinition other = messages.get(id);
        if (other != null) {
            throwValidateError("消息ID不能重复:" + id, other);
        }

        messages.put(id, this);

        super.validate();
    }
}
