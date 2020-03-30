package quan.definition.data;

import quan.definition.BeanDefinition;
import quan.definition.Constants;
import quan.definition.Category;

import java.util.regex.Pattern;

/**
 * 数据定义
 * Created by quanchangnai on 2019/6/22.
 */
public class DataDefinition extends BeanDefinition {

    private String keyType;

    private String keyName;

    private boolean persistent = true;

    {
        category = Category.data;
    }

    public DataDefinition() {
    }

    public DataDefinition(String keyName, String persistent) {
        this.keyName = keyName;
        if (persistent != null && persistent.equals("false")) {
            this.persistent = false;
        }
    }

    @Override
    public DataDefinition setCategory(Category category) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Pattern namePattern() {
        return Constants.DATA_NAME_PATTERN;
    }

    public String getKeyType() {
        return keyType;
    }

    public DataDefinition setKeyType(String keyType) {
        this.keyType = keyType;
        return this;
    }

    public String getKeyName() {
        return keyName;
    }

    public DataDefinition setKeyName(String keyName) {
        if (keyName == null || keyName.trim().equals("")) {
            return this;
        }
        this.keyName = keyName;
        return this;
    }

    @Override
    public int getDefinitionType() {
        return 5;
    }

    @Override
    public String getDefinitionTypeName() {
        return "数据";
    }

    public boolean isPersistent() {
        return persistent;
    }

    public DataDefinition setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    @Override
    public void validate() {
        super.validate();
        if (getKeyName() == null) {
            addValidatedError(getValidatedName() + "的主键不能为空");
            return;
        }
        if (getFields().stream().noneMatch(t -> t.getName().equals(getKeyName()))) {
            addValidatedError(getValidatedName() + "的主键[" + getKeyName() + "]不存在");
        }
    }

    @Override
    protected boolean isReservedWord(String fieldName) {
        if (super.isReservedWord(fieldName)) {
            return true;
        }
        return fieldName.equals("key");
    }
}
