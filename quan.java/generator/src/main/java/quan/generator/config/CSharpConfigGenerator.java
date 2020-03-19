package quan.generator.config;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConstantDefinition;
import quan.generator.util.CSharpUtils;

import java.util.Properties;

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

    public CSharpConfigGenerator() {
        super();
    }

    public CSharpConfigGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected Language supportLanguage() {
        return Language.cs;
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
                constantDefinition.getImports().add(valueField.getValueBean().getFullPackageName(supportLanguage()));
            }
        } else if (!valueField.isBuiltinType()) {
            constantDefinition.getImports().add(valueField.getClassDefinition().getFullPackageName(supportLanguage()));
        }
    }

}
