package quan.generator.config;

import org.apache.commons.cli.CommandLine;
import quan.config.TableType;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConstantDefinition;
import quan.generator.util.CSharpUtils;
import quan.generator.util.CommandLineUtils;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class CSharpConfigGenerator extends ConfigGenerator {

    {
        basicTypes.put("bool", "bool");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "string");
        basicTypes.put("set", "ISet");
        basicTypes.put("list", "IList");
        basicTypes.put("map", "IDictionary");
        basicTypes.put("date", "DateTime");
        basicTypes.put("time", "DateTime");
        basicTypes.put("datetime", "DateTime");

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
        classTypes.put("date", "DateTime");
        classTypes.put("time", "DateTime");
        classTypes.put("datetime", "DateTime");
    }

    public CSharpConfigGenerator(String codePath) {
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
        CSharpUtils.processBeanFieldImports(beanDefinition, fieldDefinition);
    }

    @Override
    protected void processConstantDependency(ConstantDefinition constantDefinition) {
        FieldDefinition valueField = constantDefinition.getValueField();
        if (valueField.isCollectionType()) {
            constantDefinition.getImports().add("System.Collections.Generic");
            if (!valueField.isBuiltinValueType()) {
                constantDefinition.getImports().add(valueField.getValueBean().getFullPackageName());
            }
        } else if (!valueField.isBuiltinType()) {
            constantDefinition.getImports().add(valueField.getClassDefinition().getFullPackageName());
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = CommandLineUtils.parseConfigArgs(CSharpConfigGenerator.class.getSimpleName(), args);
        if (commandLine == null) {
            return;
        }

        CSharpConfigGenerator generator = new CSharpConfigGenerator(commandLine.getOptionValue(CommandLineUtils.codePath));
        generator.useXmlDefinitionParser(Arrays.asList(commandLine.getOptionValues(CommandLineUtils.definitionPath)), commandLine.getOptionValue(CommandLineUtils.packagePrefix))
                .setEnumPackagePrefix(commandLine.getOptionValue(CommandLineUtils.enumPackagePrefix));

        generator.initConfigLoader(commandLine.getOptionValue(CommandLineUtils.tableType), commandLine.getOptionValue(CommandLineUtils.tablePath));

        generator.generate();
    }
}
