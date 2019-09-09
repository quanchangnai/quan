package quan.definition.data;

import quan.definition.BeanDefinition;
import quan.definition.DefinitionCategory;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class DataDefinition extends BeanDefinition {

    private String keyType;

    private String keyName;

    private boolean persistent = true;

    {
        category = DefinitionCategory.data;
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
    public DataDefinition setCategory(DefinitionCategory category) {
        throw new UnsupportedOperationException();
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
            addValidatedError(getName4Validate() + "的主键不能为空");
            return;
        }
        if (getFields().stream().noneMatch(t -> t.getName().equals(getKeyName()))) {
            addValidatedError(getName4Validate() + "的主键[" + getKeyName() + "]不存在");
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
