package quan.generator.config;

import quan.generator.Definition;
import quan.generator.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class IndexDefinition extends Definition {

    //索引类型
    private String type;

    //被索引的字段名
    private String fieldNames;

    //被索引的字段
    private List<FieldDefinition> fields = new ArrayList<>();

    private ConfigDefinition configDefinition;

    public IndexDefinition() {
    }

    public IndexDefinition(ConfigDefinition configDefinition) {
        this.configDefinition = configDefinition;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public IndexDefinition setConfigDefinition(ConfigDefinition configDefinition) {
        this.configDefinition = configDefinition;
        return this;
    }

    @Override
    public int getDefinitionType() {
        return 7;
    }

    public boolean isUnique() {
        if (type.equals("unique") || type.equals("u")) {
            return true;
        }
        return false;
    }

    public boolean isNormal() {
        if (type.equals("normal") || type.equals("n")) {
            return true;
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public IndexDefinition setType(String type) {
        this.type = type;
        return this;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public boolean addField(FieldDefinition fieldDefinition) {
        if (fields.contains(fieldDefinition)) {
            return false;
        }
        return fields.add(fieldDefinition);
    }

    public String getFieldNames() {
        return fieldNames;
    }

    public IndexDefinition setFieldNames(String fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public IndexDefinition copy(ConfigDefinition configDefinition) {
        IndexDefinition indexDefinition = new IndexDefinition();
        indexDefinition.setName(getName());
        indexDefinition.type = type;
        indexDefinition.fieldNames = fieldNames;
        indexDefinition.setComment(getComment());
        indexDefinition.setConfigDefinition(configDefinition);
        for (FieldDefinition fieldDefinition : fields) {
            indexDefinition.addField(fieldDefinition.copy(configDefinition));
        }
        return indexDefinition;
    }
}
