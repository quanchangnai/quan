package quan.definition.message;

import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.Category;

import java.util.regex.Pattern;

/**
 * 消息定义
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
        return KIND_MESSAGE;
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

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getMessageNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
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
    }


    @Override
    protected boolean isReservedWord(String fieldName) {
        if (super.isReservedWord(fieldName)) {
            return true;
        }
        return fieldName.equals("id");
    }

}
