package quan.generator.message;

import org.apache.commons.cli.CommandLine;
import quan.definition.*;
import quan.definition.parser.DefinitionParser;
import quan.generator.util.CSharpUtils;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    {
        basicTypes.put("bool", "bool");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "string");
        basicTypes.put("set", "HashSet");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Dictionary");
        basicTypes.put("bytes", "byte[]");

        classTypes.put("bool", "bool");
        classTypes.put("short", "short");
        classTypes.put("int", "int");
        classTypes.put("long", "long");
        classTypes.put("float", "float");
        classTypes.put("double", "double");
        classTypes.put("string", "string");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "List");
        classTypes.put("map", "Dictionary");
        classTypes.put("bytes", "byte[]");
    }

    public CSharpMessageGenerator(String codePath) {
        super(codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    protected void processClassSelf(ClassDefinition classDefinition) {
        classDefinition.setRealPackageName(CSharpUtils.toCapitalCamel(classDefinition.getPackageName()));
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        CSharpUtils.processBeanFieldImports(beanDefinition, fieldDefinition);
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseMessageArgs(CSharpMessageGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        CSharpMessageGenerator generator = new CSharpMessageGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        DefinitionParser definitionParser = generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix));
        definitionParser.setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));
        generator.setRecalcIdOnConflicted(commandLine.hasOption(CommandLineUtils.recalcId));
        generator.generate();
    }
}
