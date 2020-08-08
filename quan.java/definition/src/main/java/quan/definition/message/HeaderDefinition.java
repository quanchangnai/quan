package quan.definition.message;

import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.FieldDefinition;

/**
 * 消息头定义，被所有消息共用，主要用于定义消息的公共字段
 * Created by quanchangnai on 2019/9/11.
 */
public class HeaderDefinition extends BeanDefinition {

    private boolean validated;

    {
        category = Category.message;
        setName("MessageHeader");
    }


    @Override
    public int getKind() {
        return 9;
    }

    public String getKindName() {
        return "消息头";
    }

    @Override
    public HeaderDefinition setCategory(Category category) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLang(String language) {
    }

    @Override
    public void validate1() {
        if (validated) {
            return;
        }
        getFields().forEach(this::validateField);
        validated = true;
    }
}
