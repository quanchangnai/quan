package quan.definition.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import quan.definition.ClassDefinition;
import quan.definition.DependentSource.DependentType;
import quan.definition.FieldDefinition;
import quan.definition.IndexDefinition;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 常量定义，支持动态读取常量值
 */
public class ConstantDefinition extends ClassDefinition {

    private ConfigDefinition configDefinition;

    //是否使用枚举或者模拟枚举实现，不支持的语言该参数没有意义
    private boolean useEnum = true;

    private String keyField;

    private String valueFieldName;

    private FieldDefinition valueField;

    private String commentField;

    private Map<String, Pair<String, String>> rows = new TreeMap<>();

    @Override
    public int getKind() {
        return 8;
    }

    @Override
    public String getKindName() {
        return "常量";
    }

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getConstantNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public void setConfigDefinition(ConfigDefinition configDefinition) {
        setParser(configDefinition.getParser());
        setPackageName(configDefinition.getPackageName());
        getPackageNames().putAll(configDefinition.getPackageNames());
        setDefinitionFile(configDefinition.getDefinitionFile());
        this.configDefinition = configDefinition;
        configDefinition.getConstantDefinitions().add(this);
    }

    public ConstantDefinition setUseEnum(String useEnum) {
        if (useEnum != null && useEnum.trim().equals("false")) {
            this.useEnum = false;
        }
        return this;
    }

    public boolean isUseEnum() {
        return useEnum;
    }

    public void setKeyField(String keyField) {
        if (!StringUtils.isBlank(keyField)) {
            this.keyField = keyField.trim();
        }
    }

    public void setValueField(String valueField) {
        if (!StringUtils.isBlank(valueField)) {
            this.valueFieldName = valueField.trim();
        }
    }

    public void setCommentField(String commentField) {
        if (!StringUtils.isBlank(commentField)) {
            this.commentField = commentField.trim();
        }
    }

    public FieldDefinition getKeyField() {
        return configDefinition.getField(keyField);
    }

    public FieldDefinition getValueField() {
        return valueField;
    }

    public void setConfigs(List<JSONObject> configs) {
        for (JSONObject config : configs) {
            String key = config.getString(keyField);
            if (key == null || !FieldDefinition.NAME_PATTERN.matcher(key).matches()) {
                continue;
            }
            String value = config.getString(valueFieldName);
            String comment = commentField == null ? "" : config.getString(commentField);
            rows.put(key, Pair.of(value, comment));
        }
    }

    public Map<String, Pair<String, String>> getRows() {
        return rows;
    }

    @Override
    public void validate2() {
        validateKeyField();
        validateValueField();

        if (commentField != null && configDefinition.getField(commentField) == null) {
            addValidatedError(getValidatedName() + "的注释字段[" + commentField + "]不存在");
        }
    }

    public void validateKeyField() {
        if (StringUtils.isBlank(keyField)) {
            addValidatedError(getValidatedName("的") + "key字段不能为空");
            return;
        }

        FieldDefinition keyFieldDefinition = configDefinition.getField(keyField);
        if (keyFieldDefinition == null) {
            addValidatedError(getValidatedName() + "的key[" + keyField + "]不是" + configDefinition.getValidatedName() + "的字段");
            return;
        }

        if (!keyFieldDefinition.isStringType()) {
            addValidatedError(getValidatedName() + "的key字段[" + keyField + "]必须是字符串类型");
        }

        IndexDefinition keyFieldIndex = configDefinition.getIndexByField1(keyFieldDefinition);
        if (keyFieldIndex == null || !keyFieldIndex.isUnique() || keyFieldIndex.getFields().size() > 1) {
            addValidatedError(getValidatedName() + "的key字段[" + keyField + "]必须是单字段唯一索引");
        }
    }

    public void validateValueField() {
        if (StringUtils.isBlank(valueFieldName)) {
            addValidatedError(getValidatedName("的") + "value字段不能为空");
            return;
        }

        FieldDefinition valueFieldDefinition = configDefinition.getField(valueFieldName);
        if (valueFieldDefinition == null) {
            addValidatedError(getValidatedName() + "的value[" + valueFieldName + "]不是" + configDefinition.getValidatedName() + "的字段");
            return;
        }

        valueField = valueFieldDefinition.clone().setOwner(this);
    }

    @Override
    public void reset() {
        super.reset();
        resetField(valueField);
    }

    @Override
    protected void validateDependents() {
        super.validateDependents();
        addDependent(DependentType.FIELD, this, valueField, valueField.getTypeBean());
        addDependent(DependentType.FIELD_VALUE, this, valueField, valueField.getValueTypeBean());
    }

}
