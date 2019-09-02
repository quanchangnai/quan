package quan.generator.database;

import freemarker.template.Template;
import org.apache.commons.cli.CommandLine;
import quan.definition.*;
import quan.generator.Generator;
import quan.generator.util.CommandLineUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("byte", "byte");
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

        CLASS_TYPES.put("byte", "Byte");
        CLASS_TYPES.put("bool", "Boolean");
        CLASS_TYPES.put("short", "Short");
        CLASS_TYPES.put("int", "Integer");
        CLASS_TYPES.put("long", "Long");
        CLASS_TYPES.put("float", "Float");
        CLASS_TYPES.put("double", "Double");
        CLASS_TYPES.put("string", "String");
        CLASS_TYPES.put("set", "SetField");
        CLASS_TYPES.put("list", "ListField");
        CLASS_TYPES.put("map", "MapField");
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public DatabaseGenerator(String codePath) {
        super(codePath);

        Template dataTemplate;
        try {
            dataTemplate = freemarkerCfg.getTemplate("data.ftl");
        } catch (IOException e) {
            logger.error("", e);
            return;
        }
        templates.put(DataDefinition.class, dataTemplate);
        templates.put(BeanDefinition.class, dataTemplate);

    }

    @Override
    public final DefinitionCategory category() {
        return DefinitionCategory.data;
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof DataDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void processBeanField(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        super.processBeanField(beanDefinition, fieldDefinition);
        if (beanDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) beanDefinition;
            if (fieldDefinition.getName().equals(dataDefinition.getKeyName())) {
                dataDefinition.setKeyType(classTypes.get(fieldDefinition.getType()));
            }
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseArgs(DatabaseGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        DatabaseGenerator generator = new DatabaseGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        DefinitionParser definitionParser = generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix));
        definitionParser.setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));
        generator.generate();
    }

}