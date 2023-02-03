package quan.generator.config;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;

import java.util.Properties;

/**
 * 生成Java代码的配置生成器
 */
public class JavaConfigGenerator extends ConfigGenerator {

    {
        basicTypes.put("bool", "boolean");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "String");
        basicTypes.put("set", "Set");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Map");
        basicTypes.put("date", "Date");
        basicTypes.put("time", "Date");
        basicTypes.put("datetime", "Date");

        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "ArrayList");
        classTypes.put("map", "HashMap");
        classTypes.put("date", "Date");
        classTypes.put("time", "Date");
        classTypes.put("datetime", "Date");

        classNames.put("Boolean", "java.lang.Boolean");
        classNames.put("Short", "java.lang.Short");
        classNames.put("Integer", "java.lang.Integer");
        classNames.put("Long", "java.lang.Long");
        classNames.put("Float", "java.lang.Float");
        classNames.put("Double", "java.lang.Double");
        classNames.put("String", "java.lang.String");
        classNames.put("Override", "java.lang.Override");
        classNames.put("SuppressWarnings", "java.lang.SuppressWarnings");
        classNames.put("Set", "java.util.Set");
        classNames.put("HashSet", "java.util.HashSet");
        classNames.put("List", "java.util.List");
        classNames.put("ArrayList", "java.util.ArrayList");
        classNames.put("Map", "java.util.Map");
        classNames.put("HashMap", "java.util.HashMap");

        classNames.put("Bean", "quan.config.Bean");
        classNames.put("Config", "quan.config.Config");
        classNames.put("JSONObject", "com.alibaba.fastjson.JSONObject");
        classNames.put("JSONArray", "com.alibaba.fastjson.JSONArray");
    }

    public JavaConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.java;
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("java.util.*");
            beanDefinition.addImport("quan.config.load.ConfigLoader");
        }
        if (beanDefinition.getParent() == null || beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("quan.config.*");
        }
        beanDefinition.addImport("com.alibaba.fastjson.*");

        super.prepareBean(beanDefinition);
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        if (fieldDefinition.isCollectionType() || fieldDefinition.isSimpleRef() && fieldDefinition.getRefIndex().isNormal()) {
            fieldDefinition.getOwner().addImport("java.util.*");
        }
    }

}
