package quan.generator.config;

import quan.generator.Definition;
import quan.generator.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class IndexDefinition extends Definition {

    //是不是唯一索引
    private String unique;

    //被索引的字段
    private List<FieldDefinition> fields = new ArrayList<>();

    private ConfigDefinition configDefinition;

    public IndexDefinition() {
    }

    public IndexDefinition(ConfigDefinition configDefinition) {
        this.configDefinition = configDefinition;
    }

    @Override
    public int getDefinitionType() {
        return 7;
    }

    public boolean isUnique() {
        if (unique.equals("unique") || unique.equals("u")) {
            return true;
        }
        return false;
    }

    public IndexDefinition setUnique(String unique) {
        this.unique = unique;
        return this;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public IndexDefinition setConfigDefinition(ConfigDefinition configDefinition) {
        this.configDefinition = configDefinition;
        return this;
    }
}
