package quan.generator.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by quanchangnai on 2020/3/15.
 */
public class LuaConfigGenerator extends ConfigGenerator {

    public LuaConfigGenerator(String codePath) {
        super(codePath);
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
    protected void generate(ClassDefinition classDefinition) {
        if (configLoader != null && classDefinition instanceof ConfigDefinition) {
            ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
            List<JSONObject> configJsons = configLoader.loadJsons(configDefinition, true);
            List<String> rows = configJsons.stream().map(o -> toLuaString(configDefinition, o)).collect(Collectors.toList());
            configDefinition.setRows(rows);
        }
        super.generate(classDefinition);
    }

    private String toLuaString(ConfigDefinition configDefinition, JSONObject object) {
        StringBuilder builder = new StringBuilder();
        fillLuaString(builder, configDefinition, object);
        return builder.toString();
    }

    private void fillLuaString(StringBuilder builder, BeanDefinition beanDefinition, JSONObject object) {
        if (object == null) {
            builder.append("{ }");
            return;
        }

        builder.append("{ ");
        boolean start = true;

        for (FieldDefinition field : beanDefinition.getFields()) {
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
                builder.append(date != null ? date.getTime() / 1000 : 0);
                builder.append(", ").append(field.getName() + "_Str").append(" = ");
                builder.append("\"").append(object.getOrDefault(field.getName() + "$Str", "")).append("\"");
            } else if (field.getType().equals("map")) {
                fillMapLuaString(builder, field, object.getJSONObject(field.getName()));
            } else if (field.getType().equals("list") || field.getType().equals("set")) {
                fillArrayLuaString(builder, field, object.getJSONArray(field.getName()));
            } else if (field.isBeanType()) {
                fillLuaString(builder, field.getBean(), object.getJSONObject(field.getName()));
            } else {
                builder.append(object.getOrDefault(field.getName(), "nil"));
            }

        }

        builder.append(" }");
    }

    private void fillMapLuaString(StringBuilder builder, FieldDefinition field, JSONObject object) {
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

            if (!field.getKeyType().equals("string")) {
                builder.append("[").append(key).append("]");
            } else {
                builder.append(key);
            }

            builder.append(" = ");

            if (field.getValueType().equals("string")) {
                builder.append("\"").append(object.getString(key)).append("\"");
            } else if (field.isBeanValueType()) {
                fillLuaString(builder, field.getValueBean(), object.getJSONObject(key));
            } else {
                builder.append(object.get(key));
            }
        }

        builder.append(" }");
    }

    private void fillArrayLuaString(StringBuilder builder, FieldDefinition field, JSONArray array) {
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
                fillLuaString(builder, field.getValueBean(), array.getJSONObject(i));
            } else {
                builder.append(array.get(i));
            }
        }

        builder.append(" }");
    }


    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseConfigArgs(LuaConfigGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        LuaConfigGenerator generator = new LuaConfigGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix))
                .setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));

        generator.initConfigLoader(commandLine.getOptionValue(CommandLineUtils.tableType), commandLine.getOptionValue(CommandLineUtils.tablePath));

        generator.generate();
    }

}
