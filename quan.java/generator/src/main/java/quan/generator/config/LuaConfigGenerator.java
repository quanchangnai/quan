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
    protected Language supportLanguage() {
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
    protected void prepareClass(ClassDefinition classDefinition) {
        if (configLoader != null && classDefinition instanceof ConfigDefinition) {
            ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
            List<JSONObject> configJsons = configLoader.loadJsons(configDefinition, true);
            List<String> rows = configJsons.stream().map(o -> configLuaString(configDefinition, o)).collect(Collectors.toList());
            configDefinition.setRows(rows);
        }
        super.prepareClass(classDefinition);
    }

    private String configLuaString(ConfigDefinition configDefinition, JSONObject object) {
        StringBuilder builder = new StringBuilder();
        beanLuaString(builder, configDefinition, object);
        return builder.toString();
    }

    private void beanLuaString(StringBuilder builder, BeanDefinition beanDefinition, JSONObject object) {
        if (object == null) {
            builder.append("nil");
            return;
        }

        BeanDefinition actualBeanDefinition = beanDefinition;
        String clazz = object.getString("class");
        if (!StringUtils.isEmpty(clazz)) {
            actualBeanDefinition = parser.getBean(clazz);
        }

        if (actualBeanDefinition == null) {
            builder.append("nil");
            return;
        }

        builder.append("{ ");

        if (!StringUtils.isEmpty(clazz)) {
            builder.append("class = ").append("\"").append(clazz).append("\", ");
        }

        boolean start = true;
        for (FieldDefinition field : actualBeanDefinition.getFields()) {
            if (!field.supportLanguage(this.supportLanguage())) {
                continue;
            }
            if (!start) {
                builder.append(", ");
            }
            start = false;

            builder.append(field.getName()).append(" = ");

            if (field.getType().equals("string")) {
                builder.append("\"").append(object.getOrDefault(field.getName(), "")).append("\"");
            } else if (field.isNumberType()) {
                builder.append(object.getOrDefault(field.getName(), "0"));
            } else if (field.isTimeType()) {
                Date date = object.getDate(field.getName());
                builder.append(date != null ? date.getTime() : 0);
                builder.append(", ").append(field.getName()).append("_ = ");
                builder.append("\"").append(object.getOrDefault(field.getName() + "_", "")).append("\"");
            } else if (field.getType().equals("map")) {
                mapLuaString(builder, field, object.getJSONObject(field.getName()));
            } else if (field.getType().equals("list") || field.getType().equals("set")) {
                arrayLuaString(builder, field, object.getJSONArray(field.getName()));
            } else if (field.isBeanType()) {
                beanLuaString(builder, field.getBean(), object.getJSONObject(field.getName()));
            } else {
                builder.append(object.getOrDefault(field.getName(), "nil"));
            }

        }

        builder.append(" }");
    }

    private void mapLuaString(StringBuilder builder, FieldDefinition field, JSONObject object) {
        if (object == null) {
            builder.append("{ }");
            return;
        }

        builder.append("{ ");
        boolean start = true;

        for (String key : object.keySet()) {
            if (!start) {
                builder.append(", ");
            }
            start = false;

            if (field.getKeyType().equals("string")) {
                builder.append(key);
            } else {
                builder.append("[").append(key).append("]");
            }

            builder.append(" = ");

            if (field.getValueType().equals("string")) {
                builder.append("\"").append(object.getString(key)).append("\"");
            } else if (field.isBeanValueType()) {
                beanLuaString(builder, field.getValueBean(), object.getJSONObject(key));
            } else {
                builder.append(object.get(key));
            }
        }

        builder.append(" }");
    }

    private void arrayLuaString(StringBuilder builder, FieldDefinition field, JSONArray array) {
        if (array == null) {
            builder.append("{ }");
            return;
        }
        builder.append("{ ");
        boolean start = true;
        for (int i = 0; i < array.size(); i++) {
            if (!start) {
                builder.append(", ");
            }
            start = false;
            if (field.getValueType().equals("string")) {
                builder.append("\"").append(array.getString(i)).append("\"");
            } else if (field.isBeanValueType()) {
                beanLuaString(builder, field.getValueBean(), array.getJSONObject(i));
            } else {
                builder.append(array.get(i));
            }
        }

        builder.append(" }");
    }
}
