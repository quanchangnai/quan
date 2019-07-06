package quan.generator.database;

import quan.generator.BeanDefinition;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class DataDefinition extends BeanDefinition {

    private String keyType;

    private String keyName;

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
        this.keyName = keyName;
        return this;
    }

    @Override
    public int getDefinitionType() {
        return 5;
    }

    @Override
    public void validate() {
        super.validate();
        if (getKeyName() == null || getKeyName().trim().equals("")) {
            throwValidateError("主键不能为空");
        }
        if (getKeyType() == null || getKeyType().trim().equals("")) {
            throwValidateError("主键不存在");
        }
    }
}
