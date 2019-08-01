package quan.generator.database;

import quan.generator.BeanDefinition;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class DataDefinition extends BeanDefinition {

    private String keyType;

    private String keyName;

    private boolean persistent = true;

    public DataDefinition() {
    }

    public DataDefinition(String keyName, String persistent) {
        this.keyName = keyName;
        if (persistent != null && persistent.equals("false")) {
            this.persistent = false;
        }
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
            addValidatedError("配置" + getName4Validate() + "的主键不能为空");
            return;
        }
        if (getFields().stream().noneMatch(t -> t.getName().equals(getKeyName()))) {
            addValidatedError("配置" + getName4Validate() + "的主键[" + getKeyName() + "]不存在");
        }
    }
}
