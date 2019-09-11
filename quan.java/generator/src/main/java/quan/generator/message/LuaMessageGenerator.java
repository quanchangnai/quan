package quan.generator.message;

import org.apache.commons.cli.CommandLine;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.message.MessageHeadDefinition;
import quan.definition.parser.DefinitionParser;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/9/5.
 */
public class LuaMessageGenerator extends MessageGenerator {

    public LuaMessageGenerator(String codePath) {
        super(codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.lua;
    }


    @Override
    protected void generate(List<ClassDefinition> classDefinitions) {
        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition instanceof MessageHeadDefinition) {
                continue;
            }
            generate(classDefinition);
        }
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        BeanDefinition fieldBean = fieldDefinition.getBean();
        if (fieldBean != null) {
            beanDefinition.getImports().add(fieldBean.getFullName());
        }

        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null) {
            beanDefinition.getImports().add(fieldValueBean.getFullName());
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseMessageArgs(LuaMessageGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        LuaMessageGenerator generator = new LuaMessageGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        DefinitionParser definitionParser = generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix));
        definitionParser.setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));
        generator.setRecalcIdOnConflicted(commandLine.hasOption(CommandLineUtils.recalcId));
        generator.generate();
    }
}
