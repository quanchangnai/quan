package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.DefinitionCategory;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class MessageDefinition extends BeanDefinition {

    //保存原始类名，因为C#等其他语言的类型会做转换
    private String originalName;

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

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    @Override
    public void validate() {
        super.validate();
        originalName = getPackageName() + "." + getName();
    }
}
