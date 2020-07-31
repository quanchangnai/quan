package quan.generator.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static quan.definition.ClassDefinition.getLongClassName;

/**
 * Created by quanchangnai on 2020/3/15.
 */
public class LuaConfigGenerator extends ConfigGenerator {

    public LuaConfigGenerator() {
    }

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
            List<JSONObject> configJsons = configLoader.loadJsons(configDefinition, true);
            List<String> rows = configJsons.stream().map(o -> configLuaString(configDefinition, o)).collect(Collectors.toList());
            configDefinition.setRows(rows);
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
            beanDefinition = parser.getBean(getLongClassName(configDefinition, clazz));
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
            if (!field.isSupportLanguage(this.language())) {
                continue;
            }
            if (!start) {
                luaBuilder.append(", ");
            }
            start = false;

            luaBuilder.append(field.getName()).append(" = ");

            if (field.getType().equals("string")) {
                luaBuilder.append("\"").append(object.getOrDefault(field.getName(), "")).append("\"");
            } else if (field.isNumberType()) {
                luaBuilder.append(object.getOrDefault(field.getName(), "0"));
            } else if (field.isTimeType()) {
                Date date = object.getDate(field.getName());
                luaBuilder.append(date != null ? date.getTime() : 0);
                luaBuilder.append(", ").append(field.getName()).append("_ = ");
                luaBuilder.append("\"").append(object.getOrDefault(field.getName() + "_", "")).append("\"");
            } else if (field.getType().equals("map")) {
                mapLuaString(configDefinition, field, object.getJSONObject(field.getName()), luaBuilder);
            } else if (field.getType().equals("list") || field.getType().equals("set")) {
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

            if (field.getKeyType().equals("string")) {
                luaBuilder.append(key);
            } else {
                luaBuilder.append("[").append(key).append("]");
            }

            luaBuilder.append(" = ");

            if (field.getValueType().equals("string")) {
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
            if (field.getValueType().equals("string")) {
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
