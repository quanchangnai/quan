package quan.generator.message;

import org.apache.commons.lang3.StringUtils;
import quan.generator.BeanDefinition;
import quan.generator.DefinitionCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private static Map<String, MessageDefinition> messages = new HashMap<>();

    private String id;

    {
        category = DefinitionCategory.data;
    }

    public MessageDefinition() {
    }

    public MessageDefinition(String id) {
        this.id = id;
    }

    @Override
    public int getDefinitionType() {
        return 3;
    }

    @Override
    public MessageDefinition setCategory(DefinitionCategory category) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefinitionTypeName() {
        return "消息";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        this.id = id.trim();
    }

    @Override
    public void validate() {
        boolean idIsInt = true;
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            idIsInt = false;
            addValidatedError(getName4Validate() + "的ID[" + id + "]必须是整数");
        }

        if (idIsInt) {
            MessageDefinition other = messages.get(id);
            if (other != null) {
                addValidatedError(getName4Validate() + "的ID[" + id + "]不能重复", other);
            }
            messages.put(id, this);
        }

        super.validate();

    }


}
