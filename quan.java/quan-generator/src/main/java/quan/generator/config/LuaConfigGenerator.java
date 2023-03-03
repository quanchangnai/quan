package quan.generator.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 生成Lua代码的配置生成器
 */
public class LuaConfigGenerator extends ConfigGenerator {

    public LuaConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.lua;
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition.getClass() == BeanDefinition.class) {
            return false;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        super.prepareBean(beanDefinition);
        if (configLoader != null && beanDefinition instanceof ConfigDefinition) {
            ConfigDefinition configDefinition = (ConfigDefinition) beanDefinition;
            configDefinition.setVersion2(configDefinition.getVersion() + ":" + configLoader.getConfigVersion(configDefinition, false));

            if (checkChange(configDefinition)) {
                List<JSONObject> configJsons = configLoader.loadJsons(configDefinition, true);
                List<String> rows = new ArrayList<>();
                for (JSONObject json : configJsons) {
                    rows.add(configLuaString(configDefinition, json));
                }
                configDefinition.setRows(rows);
            }
        }
    }

    @Override
    protected boolean isChange(ClassDefinition classDefinition) {
        if (classDefinition instanceof ConfigDefinition) {
            String fullName = classDefinition.getFullName(language());
            String version = ((ConfigDefinition) classDefinition).getVersion2();
            return !version.equals(oldRecords.get(fullName));
        }
        return super.isChange(classDefinition);
    }

    @Override
    public void putRecord(ClassDefinition classDefinition) {
        if (classDefinition instanceof ConfigDefinition) {
            String fullName = classDefinition.getFullName(language());
            String version = ((ConfigDefinition) classDefinition).getVersion2();
            oldRecords.remove(fullName);
            newRecords.put(fullName, version);
        } else {
            super.putRecord(classDefinition);
        }
    }

    private String configLuaString(ConfigDefinition configDefinition, JSONObject object) {
        StringBuilder luaBuilder = new StringBuilder();
        beanLuaString(configDefinition, configDefinition, object, luaBuilder);
        return luaBuilder.toString();
    }

    private void beanLuaString(ConfigDefinition configDefinition, BeanDefinition beanDefinition, JSONObject object, StringBuilder luaBuilder) {
        if (object == null) {
            luaBuilder.append("nil");
            return;
        }

        String clazz = object.getString("class");
        if (!StringUtils.isEmpty(clazz)) {
            beanDefinition = parser.getBean(configDefinition, clazz);
        }

        if (beanDefinition == null) {
            luaBuilder.append("nil");
            return;
        }

        luaBuilder.append("{ ");

        if (!StringUtils.isEmpty(clazz)) {
            luaBuilder.append("class = ").append("\"").append(clazz).append("\", ");
        }

        boolean start = true;
        for (FieldDefinition field : beanDefinition.getFields()) {
            if (!field.isSupportedLanguage(this.language())) {
                continue;
            }
            if (!start) {
                luaBuilder.append(", ");
            }
            start = false;

            luaBuilder.append(field.getName()).append(" = ");

            if (field.isStringType()) {
                luaBuilder.append("\"").append(object.getOrDefault(field.getName(), "")).append("\"");
            } else if (field.isNumberType()) {
                luaBuilder.append(object.getOrDefault(field.getName(), "0"));
            } else if (field.isTimeType()) {
                Date date = object.getDate(field.getName());
                luaBuilder.append(date != null ? date.getTime() : 0);
                luaBuilder.append(", ").append(field.getName()).append("_ = ");
                luaBuilder.append("\"").append(object.getOrDefault(field.getName() + "_", "")).append("\"");
            } else if (field.isMapType()) {
                mapLuaString(configDefinition, field, object.getJSONObject(field.getName()), luaBuilder);
            } else if (field.isListType() || field.isSetType()) {
                arrayLuaString(configDefinition, field, object.getJSONArray(field.getName()), luaBuilder);
            } else if (field.isBeanType()) {
                beanLuaString(configDefinition, field.getTypeBean(), object.getJSONObject(field.getName()), luaBuilder);
            } else {
                luaBuilder.append(object.getOrDefault(field.getName(), "nil"));
            }

        }

        luaBuilder.append(" }");
    }

    private void mapLuaString(ConfigDefinition configDefinition, FieldDefinition field, JSONObject object, StringBuilder luaBuilder) {
        if (object == null) {
            luaBuilder.append("{ }");
            return;
        }

        luaBuilder.append("{ ");
        boolean start = true;

        for (String key : object.keySet()) {
            if (!start) {
                luaBuilder.append(", ");
            }
            start = false;

            if (field.isStringKeyType()) {
                luaBuilder.append(key);
            } else {
                luaBuilder.append("[").append(key).append("]");
            }

            luaBuilder.append(" = ");

            if (field.isStringValueType()) {
                luaBuilder.append("\"").append(object.getString(key)).append("\"");
            } else if (field.isBeanValueType()) {
                beanLuaString(configDefinition, field.getValueTypeBean(), object.getJSONObject(key), luaBuilder);
            } else {
                luaBuilder.append(object.get(key));
            }
        }

        luaBuilder.append(" }");
    }

    private void arrayLuaString(ConfigDefinition configDefinition, FieldDefinition field, JSONArray array, StringBuilder luaBuilder) {
        if (array == null) {
            luaBuilder.append("{ }");
            return;
        }
        luaBuilder.append("{ ");
        boolean start = true;
        for (int i = 0; i < array.size(); i++) {
            if (!start) {
                luaBuilder.append(", ");
            }
            start = false;
            if (field.isStringValueType()) {
                luaBuilder.append("\"").append(array.getString(i)).append("\"");
            } else if (field.isBeanValueType()) {
                beanLuaString(configDefinition, field.getValueTypeBean(), array.getJSONObject(i), luaBuilder);
            } else {
                luaBuilder.append(array.get(i));
            }
        }

        luaBuilder.append(" }");
    }

}
