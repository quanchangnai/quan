package quan.generator.database;

import freemarker.template.Template;
import org.apache.commons.cli.CommandLine;
import quan.definition.*;
import quan.definition.data.DataDefinition;
import quan.generator.Generator;
import quan.generator.util.CommandLineUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    {
        basicTypes.put("byte", "byte");
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

        classTypes.put("byte", "Byte");
        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "SetField");
        classTypes.put("list", "ListField");
        classTypes.put("map", "MapField");
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
