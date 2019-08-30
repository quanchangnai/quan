package quan.generator.message;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import quan.generator.*;
import quan.generator.util.CSharpUtils;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("bool" , "bool" );
        BASIC_TYPES.put("short" , "short" );
        BASIC_TYPES.put("int" , "int" );
        BASIC_TYPES.put("long" , "long" );
        BASIC_TYPES.put("float" , "float" );
        BASIC_TYPES.put("double" , "double" );
        BASIC_TYPES.put("string" , "string" );
        BASIC_TYPES.put("set" , "HashSet" );
        BASIC_TYPES.put("list" , "List" );
        BASIC_TYPES.put("map" , "Dictionary" );
        BASIC_TYPES.put("bytes" , "byte[]" );

        CLASS_TYPES.put("bool" , "bool" );
        CLASS_TYPES.put("short" , "short" );
        CLASS_TYPES.put("int" , "int" );
        CLASS_TYPES.put("long" , "long" );
        CLASS_TYPES.put("float" , "float" );
        CLASS_TYPES.put("double" , "double" );
        CLASS_TYPES.put("string" , "string" );
        CLASS_TYPES.put("set" , "HashSet" );
        CLASS_TYPES.put("list" , "List" );
        CLASS_TYPES.put("map" , "Dictionary" );
        CLASS_TYPES.put("bytes" , "byte[]" );
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public CSharpMessageGenerator(String codePath) {
        super(codePath);
    }


    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    protected void processClassSelf(ClassDefinition classDefinition) {
        classDefinition.setPackageName(CSharpUtils.toCapitalCamel(classDefinition.getPackageName()));
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        CSharpUtils.processBeanFieldImports(definitionParser, beanDefinition, fieldDefinition);
    }

    public static void main(String[] args) {
        Option recalcIdOption = new Option(null, "recalcId" , false, "哈希计算消息ID冲突时是否重新计算(可选)" );
        CommandLine commandLine = CommandLineUtils.parseCommandLine(CSharpMessageGenerator.class.getSimpleName() , args, recalcIdOption);
        if (commandLine == null) {
            return;
        }

        CSharpMessageGenerator generator = new CSharpMessageGenerator(commandLine.getOptionValue("codePath" ));
        DefinitionParser definitionParser = generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues("definitionPath" )), commandLine.getOptionValue("packagePrefix" ));
        definitionParser.setEnumPackagePrefix(commandLine.getOptionValue("enumPackagePrefix" ));
        generator.setRecalcIdOnConflicted(commandLine.hasOption("recalcId" ));
        generator.generate();
    }
}
