package quan.definition.message;

import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息定义
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private int id;

    {
        category = Category.message;

    }

    public MessageDefinition() {
    }

    @Override
    public int getDefinitionType() {
        return 3;
    }

    @Override
    public MessageDefinition setCategory(Category category) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefinitionTypeName() {
        return "消息";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        MessageHeadDefinition headDefinition = parser.getMessageHead();
        if (fieldDefinition.getName() != null && headDefinition != null) {
            headDefinition.validate();
            if (headDefinition.getField(fieldDefinition.getName()) != null) {
                addValidatedError(getValidatedName("的") + "字段名[" + fieldDefinition.getName() + "]不能和消息头的字段重复");
            }
        }
        super.validateFieldNameDuplicate(fieldDefinition);
    }

    @Override
    protected boolean isReservedWord(String fieldName) {
        if (super.isReservedWord(fieldName)) {
            return true;
        }
        return fieldName.equals("id");
    }

    public MessageHeadDefinition getHead() {
        return parser.getMessageHead();
    }

    public List<FieldDefinition> getHeadedFields() {
        List<FieldDefinition> headedFields = new ArrayList<>();
        if (getHead() != null) {
            headedFields.addAll(getHead().getFields());
        }
        headedFields.addAll(getFields());
        return headedFields;
    }
}
