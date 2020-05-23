package quan.definition.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import quan.definition.ClassDefinition;
import quan.definition.Constants;
import quan.definition.FieldDefinition;
import quan.definition.IndexDefinition;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 常量定义，支持动态读取常量值
 * Created by quanchangnai on 2019/9/6.
 */
public class ConstantDefinition extends ClassDefinition {

    private ConfigDefinition configDefinition;

    //是否使用枚举或者模拟枚举实现，不支持的语言该参数没有意义
    private boolean useEnum = true;

    private String keyField;

    private String valueField;

    private String commentField;

    private Map<String, Pair<String, String>> rows = new TreeMap<>();

    @Override
    public int getDefinitionType() {
        return 8;
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
        getPackageNames().putAll(configDefinition.getPackageNames());
        setDefinitionFile(configDefinition.getDefinitionFile());
        this.configDefinition = configDefinition;
        configDefinition.getConstantDefinitions().add(this);
    }

    public ConstantDefinition setUseEnum(String useEnum) {
        if (useEnum != null && useEnum.equals("false")) {
            this.useEnum = false;
        }
        return this;
    }

    public boolean isUseEnum() {
        return useEnum;
    }

    public void setKeyField(String keyField) {
        if (!StringUtils.isBlank(keyField)) {
            this.keyField = keyField;
        }
    }

    public void setValueField(String valueField) {
        if (!StringUtils.isBlank(valueField)) {
            this.valueField = valueField;
        }
    }

    public void setCommentField(String commentField) {
        if (!StringUtils.isBlank(commentField)) {
            this.commentField = commentField;
        }
    }

    public FieldDefinition getKeyField() {
        return configDefinition.getField(keyField);
    }

    public FieldDefinition getValueField() {
        return configDefinition.getField(valueField);
    }


    public void setConfigs(List<JSONObject> configs) {
        for (JSONObject config : configs) {
            String key = config.getString(keyField);
            if (key == null || !Constants.FIELD_NAME_PATTERN.matcher(key).matches()) {
                continue;
            }
            String value = config.getString(valueField);
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

        if (!keyFieldDefinition.getType().equals("string")) {
            addValidatedError(getValidatedName() + "的key字段[" + keyField + "]必须是字符串类型");
        }

        IndexDefinition keyFieldIndex = configDefinition.getIndexByField1(keyFieldDefinition);
        if (keyFieldIndex == null || !keyFieldIndex.isUnique() || keyFieldIndex.getFields().size() > 1) {
            addValidatedError(getValidatedName() + "的key字段[" + keyField + "]必须是单字段唯一索引");
        }
    }

    public void validateValueField() {
        if (StringUtils.isBlank(valueField)) {
            addValidatedError(getValidatedName("的") + "value字段不能为空");
            return;
        }

        FieldDefinition valueFieldDefinition = configDefinition.getField(valueField);
        if (valueFieldDefinition == null) {
            addValidatedError(getValidatedName() + "的value[" + valueField + "]不是" + configDefinition.getValidatedName() + "的字段");
        }
    }

}
