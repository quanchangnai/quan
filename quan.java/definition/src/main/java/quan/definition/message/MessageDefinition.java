package quan.definition.message;

import org.apache.commons.lang3.StringUtils;
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

    //支持定义消息ID，一般情况下由哈希生成
    private String strId;

    private int id;

    {
        category = Category.message;

    }

    public MessageDefinition(String strId) {
        if (!StringUtils.isBlank(strId)) {
            this.strId = strId.trim();
        }
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


    public boolean isDefinedId() {
        return !StringUtils.isBlank(strId);
    }

    @Override
    public void validate() {
        if (strId != null) {
            try {
                id = Integer.parseInt(strId);
            } catch (NumberFormatException e) {
                addValidatedError(getValidatedName("的") + "ID[" + strId + "]不合法");
            }
        }
        super.validate();
    }

    @Override
    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        HeadDefinition headDefinition = parser.getMessageHead();
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

    public HeadDefinition getHead() {
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
