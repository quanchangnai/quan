package quan.generator.database;

import quan.generator.BeanDefinition;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class DataDefinition extends BeanDefinition {

    private String primaryKeyType;

    private String primaryKeyName;

    public String getPrimaryKeyType() {
        return primaryKeyType;
    }

    public DataDefinition setPrimaryKeyType(String primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
        return this;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public DataDefinition setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
        return this;
    }

    @Override
    public int getDefinitionType() {
        return 5;
    }

}
