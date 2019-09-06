package quan.definition.config;

import org.apache.commons.lang3.StringUtils;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;

/**
 * Created by quanchangnai on 2019/9/6.
 */
public class ConstantDefinition extends ClassDefinition {

    private ConfigDefinition configDefinition;

    private String keyField;

    private String valueField;

    @Override
    public int getDefinitionType() {
        return 8;
    }

    @Override
    public String getName4Validate() {
        return super.getName4Validate();
    }

    @Override
    public String getDefinitionTypeName() {
        return "常量";
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public void setConfigDefinition(ConfigDefinition configDefinition) {
        setParser(configDefinition.getParser());
        setPackageName(configDefinition.getPackageName());
        setPackagePrefix(configDefinition.getPackagePrefix());
        setDefinitionFile(configDefinition.getDefinitionFile());
        configDefinition.getConstants().add(this);
        this.configDefinition = configDefinition;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public FieldDefinition getKeyField() {
        return configDefinition.getField(keyField);
    }

    public FieldDefinition getValueField() {
        return configDefinition.getField(valueField);
    }

    @Override
    public void validate2() {
        validateKeyField();
        validateValueField();
    }

    public void validateKeyField() {
        FieldDefinition keyFieldDefinition = configDefinition.getField(keyField);
        if (StringUtils.isBlank(keyField)) {
            addValidatedError(getName4Validate("的") + "key字段不能为空");
            return;
        }
        if (keyFieldDefinition == null) {
            addValidatedError(getName4Validate() + "的key[" + keyField + "]不是" + configDefinition.getName4Validate() + "的字段");
            return;
        }
        if (!keyFieldDefinition.getType().equals("string")) {
            addValidatedError(getName4Validate() + "的key字段[" + keyField + "]必须是字符串类型");
        }

        IndexDefinition keyFieldIndex = configDefinition.getIndexByField1(keyFieldDefinition);
        if (keyFieldIndex == null || !keyFieldIndex.isUnique() || keyFieldIndex.getFields().size() > 1) {
            addValidatedError(getName4Validate() + "的key字段[" + keyField + "]必须是单字段唯一索引");
        }
    }

    public void validateValueField() {
        FieldDefinition valueFieldDefinition = configDefinition.getField(valueField);
        if (StringUtils.isBlank(valueField)) {
            addValidatedError(getName4Validate("的") + "value字段不能为空");
            return;
        }
        if (valueFieldDefinition == null) {
            addValidatedError(getName4Validate() + "的value[" + valueField + "]不是" + configDefinition.getName4Validate() + "的字段");
            return;
        }
    }
}
