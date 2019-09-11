package quan.definition.message;

import quan.definition.BeanDefinition;
import quan.definition.DefinitionCategory;
import quan.definition.FieldDefinition;

/**
 * 消息头定义，被所有消息共用
 * Created by quanchangnai on 2019/9/11.
 */
public class MessageHeadDefinition extends BeanDefinition {

    private boolean validated;

    {
        category = DefinitionCategory.message;
        setName("HeadedMessage");
    }


    @Override
    public int getDefinitionType() {
        return 9;
    }

    public String getDefinitionTypeName() {
        return "消息头";
    }

    @Override
    public MessageHeadDefinition setCategory(DefinitionCategory category) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLang(String language) {
    }

    @Override
    public void validate() {
        if (validated) {
            return;
        }
        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
        validated = true;
    }
}
