package quan.definition.message;

import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.DependentSource.DependentType;
import quan.definition.FieldDefinition;

/**
 * 消息定义
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    //支持自定义消息ID，一般情况下由哈希生成
    private String strId;

    private int id;

    {
        category = Category.message;
    }

    public MessageDefinition(String id) {
        if (!StringUtils.isBlank(id)) {
            this.strId = id.trim();
        }
    }

    @Override
    public int getKind() {
        return 3;
    }

    @Override
    public MessageDefinition setCategory(Category category) {
        if (category != this.category) {
            throw new IllegalStateException();
        }
        return this;
    }

    @Override
    public String getKindName() {
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
    public void validate1() {
        super.validate1();

        if (strId != null) {
            try {
                id = Integer.parseInt(strId);
            } catch (NumberFormatException e) {
                addValidatedError(getValidatedName("的") + "ID[" + strId + "]不合法");
            }
        }

        if (getHeader() != null) {
            fields.addAll(0, getHeader().getFields());
        }
    }

    @Override
    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        HeaderDefinition headerDefinition = parser.getMessageHeader();
        if (fieldDefinition.getName() != null && headerDefinition != null) {
            headerDefinition.validate1();
            if (headerDefinition.getField(fieldDefinition.getName()) != null) {
                addValidatedError(getValidatedName("的") + "字段名[" + fieldDefinition.getName() + "]不能和消息头的字段重复");
            }
        }
        super.validateFieldNameDuplicate(fieldDefinition);
    }

    @Override
    protected void validateDependents() {
        HeaderDefinition header = getHeader();
        if (header != null) {
            addDependent(DependentType.MESSAGE_HEADER, this, header, header);
        }
        super.validateDependents();
    }

    @Override
    protected boolean isReservedWord(String fieldName) {
        if (super.isReservedWord(fieldName)) {
            return true;
        }
        return fieldName.equals("id");
    }

    public HeaderDefinition getHeader() {
        return parser.getMessageHeader();
    }

}
