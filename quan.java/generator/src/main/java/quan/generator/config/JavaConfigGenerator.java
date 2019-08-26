package quan.generator.config;

import quan.generator.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class JavaConfigGenerator extends ConfigGenerator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("bool", "boolean");
        BASIC_TYPES.put("short", "short");
        BASIC_TYPES.put("int", "int");
        BASIC_TYPES.put("long", "long");
        BASIC_TYPES.put("float", "float");
        BASIC_TYPES.put("double", "double");
        BASIC_TYPES.put("string", "String");
        BASIC_TYPES.put("set", "Set");
        BASIC_TYPES.put("list", "List");
        BASIC_TYPES.put("map", "Map");
        BASIC_TYPES.put("date", "Date");
        BASIC_TYPES.put("time", "Date");
        BASIC_TYPES.put("datetime", "Date");

        CLASS_TYPES.put("bool", "Boolean");
        CLASS_TYPES.put("short", "Short");
        CLASS_TYPES.put("int", "Integer");
        CLASS_TYPES.put("long", "Long");
        CLASS_TYPES.put("float", "Float");
        CLASS_TYPES.put("double", "Double");
        CLASS_TYPES.put("string", "String");
        CLASS_TYPES.put("set", "HashSet");
        CLASS_TYPES.put("list", "ArrayList");
        CLASS_TYPES.put("map", "HashMap");
        CLASS_TYPES.put("date", "Date");
        CLASS_TYPES.put("time", "Date");
        CLASS_TYPES.put("datetime", "Date");
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public JavaConfigGenerator(String codePath) throws Exception {
        super(codePath);
    }


    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    public static void main(String[] args) throws Exception {

        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("generator\\definition\\config");
        String codePath = "config\\src\\test\\java";
        String packagePrefix = "quan.config";

        JavaConfigGenerator generator = new JavaConfigGenerator(codePath);
        generator.useXmlDefinitionParser(definitionPaths, packagePrefix);

        generator.generate();
    }
}
