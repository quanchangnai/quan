package quan.generator.message;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionParser;
import quan.generator.Language;
import quan.generator.util.CommandLineUtils;
import quan.message.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("bool" , "boolean" );
        BASIC_TYPES.put("short" , "short" );
        BASIC_TYPES.put("int" , "int" );
        BASIC_TYPES.put("long" , "long" );
        BASIC_TYPES.put("float" , "float" );
        BASIC_TYPES.put("double" , "double" );
        BASIC_TYPES.put("string" , "String" );
        BASIC_TYPES.put("set" , "Set" );
        BASIC_TYPES.put("list" , "List" );
        BASIC_TYPES.put("map" , "Map" );
        BASIC_TYPES.put("bytes" , "byte[]" );

        CLASS_TYPES.put("bool" , "Boolean" );
        CLASS_TYPES.put("short" , "Short" );
        CLASS_TYPES.put("int" , "Integer" );
        CLASS_TYPES.put("long" , "Long" );
        CLASS_TYPES.put("float" , "Float" );
        CLASS_TYPES.put("double" , "Double" );
        CLASS_TYPES.put("string" , "String" );
        CLASS_TYPES.put("set" , "HashSet" );
        CLASS_TYPES.put("list" , "ArrayList" );
        CLASS_TYPES.put("map" , "HashMap" );
        CLASS_TYPES.put("bytes" , "byte[]" );
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public JavaMessageGenerator(String codePath) {
        super(codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    @Override
    protected void processClassSelf(ClassDefinition classDefinition) {
        super.processClassSelf(classDefinition);
        if (!(classDefinition instanceof BeanDefinition)) {
            return;
        }

        BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
        beanDefinition.getImports().add(Message.class.getPackage().getName() + ".*" );
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseMessageCommandLine(JavaMessageGenerator.class.getSimpleName(), args);
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
