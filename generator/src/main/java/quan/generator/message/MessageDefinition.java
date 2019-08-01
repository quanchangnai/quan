package quan.generator.message;

import quan.generator.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private static Map<String, MessageDefinition> all = new HashMap<>();

    private String id;

    public MessageDefinition() {
    }

    public MessageDefinition(String id) {
        this.id = id;
    }

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
        boolean idIsInt = true;
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            idIsInt = false;
            addValidatedError("消息" + getName4Validate() + "ID[" + id + "]必须是整数");
        }

        if (idIsInt) {
            MessageDefinition other = all.get(id);
            if (other != null) {
                addValidatedError("消息" + getName4Validate() + "ID[" + id + "]不能重复", other);
            }
            all.put(id, this);
        }

        super.validate();

    }
}
