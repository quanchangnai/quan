package quan.generator.message;

import org.apache.commons.cli.CommandLine;
import quan.definition.parser.DefinitionParser;
import quan.definition.Language;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

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
        basicTypes.put("bytes", "byte[]");

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
        classTypes.put("bytes", "byte[]");
    }

    public JavaMessageGenerator(String codePath) {
        super(codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }


    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseMessageArgs(JavaMessageGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        JavaMessageGenerator generator = new JavaMessageGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        DefinitionParser definitionParser = generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix));
        definitionParser.setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));
        generator.setRecalcIdOnConflicted(commandLine.hasOption(CommandLineUtils.recalcId));
        generator.generate();
    }
}
