package quan.definition.config;

import org.apache.commons.lang3.StringUtils;
import quan.definition.Constants;
import quan.definition.Definition;
import quan.definition.FieldDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    @Override
    public int getDefinitionType() {
        return 7;
    }

    @Override
    public String getDefinitionTypeName() {
        return "索引";
    }

    @Override
    protected Pattern namePattern() {
        return Constants.FIELD_NAME_PATTERN;
    }

    public boolean isUnique() {
        return type.equals("unique" ) || type.equals("u" );
    }

    public boolean isNormal() {
        return type.equals("normal" ) || type.equals("n" );
    }

    public String getType() {
        return type;
    }

    public IndexDefinition setType(String type) {
        if (StringUtils.isBlank(type)) {
            return this;
        }
        this.type = type.trim();
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
        if (StringUtils.isBlank(fieldNames)) {
            return this;
        }
        this.fieldNames = fieldNames.trim();
        return this;
    }
}
