package quan.generator.config;

import quan.definition.Language;
import quan.definition.config.ConstantDefinition;

import java.util.Properties;

/**
 * Created by quanchangnai on 2019/7/11.
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
    }

    public JavaConfigGenerator() {
    }

    public JavaConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.java;
    }

    @Override
    protected void prepareConstant(ConstantDefinition constantDefinition) {
        super.prepareConstant(constantDefinition);
        if (constantDefinition.getValueField().isCollectionType()) {
            constantDefinition.getImports().put("java.util.*",null);
        }
    }
}
