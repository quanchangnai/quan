package quan.generator.config;

import quan.generator.*;
import quan.generator.util.CSharpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class CSharpConfigGenerator extends ConfigGenerator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("bool", "bool");
        BASIC_TYPES.put("short", "short");
        BASIC_TYPES.put("int", "int");
        BASIC_TYPES.put("long", "long");
        BASIC_TYPES.put("float", "float");
        BASIC_TYPES.put("double", "double");
        BASIC_TYPES.put("string", "string");
        BASIC_TYPES.put("set", "ISet");
        BASIC_TYPES.put("list", "IList");
        BASIC_TYPES.put("map", "IDictionary");
        BASIC_TYPES.put("date", "DateTime");
        BASIC_TYPES.put("time", "DateTime");
        BASIC_TYPES.put("datetime", "DateTime");

        CLASS_TYPES.put("bool", "bool");
        CLASS_TYPES.put("short", "short");
        CLASS_TYPES.put("int", "int");
        CLASS_TYPES.put("long", "long");
        CLASS_TYPES.put("float", "float");
        CLASS_TYPES.put("double", "double");
        CLASS_TYPES.put("string", "string");
        CLASS_TYPES.put("set", "HashSet");
        CLASS_TYPES.put("list", "List");
        CLASS_TYPES.put("map", "Dictionary");
        CLASS_TYPES.put("date", "DateTime");
        CLASS_TYPES.put("time", "DateTime");
        CLASS_TYPES.put("datetime", "DateTime");
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public CSharpConfigGenerator(String codePath) throws Exception {
        super(codePath);
    }


    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    protected void processClassSelf(ClassDefinition classDefinition) {
        classDefinition.setPackageName(CSharpUtils.namespace(classDefinition.getPackageName()));
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        CSharpUtils.processBeanFieldImports(definitionParser, beanDefinition, fieldDefinition);
    }

    public static void main(String[] args) throws Exception {

        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("generator\\definition\\config");
        String codePath = "..\\quan-cs";
        String packagePrefix = "ConfigCS.Test";

        CSharpConfigGenerator generator = new CSharpConfigGenerator(codePath);
        generator.useXmlDefinitionParser(definitionPaths, packagePrefix);

        generator.generate();
    }
}
