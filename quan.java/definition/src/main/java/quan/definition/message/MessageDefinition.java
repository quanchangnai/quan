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

    //支持自定义消息ID，一般情况下由哈希生成
    private String sid;

    private int id;

    //消息所有字段，包含消息头
    private List<FieldDefinition> allFields = new ArrayList<>();

    {
        category = Category.message;
    }

    public MessageDefinition(String sid) {
        if (!StringUtils.isBlank(sid)) {
            this.sid = sid.trim();
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
        return !StringUtils.isBlank(sid);
    }

    @Override
    public void validate() {
        if (sid != null) {
            try {
                id = Integer.parseInt(sid);
            } catch (NumberFormatException e) {
                addValidatedError(getValidatedName("的") + "ID[" + sid + "]不合法");
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

    public List<FieldDefinition> getAllFields() {
        if (allFields.isEmpty()) {
            if (getHead() != null) {
                allFields.addAll(getHead().getFields());
            }
            allFields.addAll(getFields());
        }
        return allFields;
    }

}
