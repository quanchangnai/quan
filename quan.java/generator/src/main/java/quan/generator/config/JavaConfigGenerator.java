package quan.generator.config;

import org.apache.commons.cli.CommandLine;
import quan.config.TableType;
import quan.definition.Language;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;

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

    public JavaConfigGenerator(String codePath) {
        super(codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseConfigArgs(JavaConfigGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        JavaConfigGenerator generator = new JavaConfigGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix))
                .setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));

        String[] tableTypeAndPath = commandLine.getOptionValues(CommandLineUtils.table);
        generator.initConfigLoader(TableType.valueOf(tableTypeAndPath[0]), tableTypeAndPath[1]);

        generator.generate();
    }

}
