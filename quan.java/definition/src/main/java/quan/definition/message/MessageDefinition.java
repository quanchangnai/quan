package quan.definition.message;

import quan.definition.BeanDefinition;
import quan.definition.DefinitionCategory;

/**
 * 消息定义
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    private int id;

    {
        category = DefinitionCategory.message;

    }

    public MessageDefinition() {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    protected boolean isReservedWord(String fieldName) {
        if (super.isReservedWord(fieldName)) {
            return true;
        }
        return fieldName.equals("id") || fieldName.equals("seq");
    }

    public MessageHeadDefinition getHead() {
        System.err.println("getHead============");
        return parser.getMessageHead();
    }
}
