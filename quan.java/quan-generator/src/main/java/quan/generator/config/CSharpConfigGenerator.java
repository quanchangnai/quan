package quan.generator.config;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;
import quan.generator.util.CSharpUtils;

import java.util.Properties;

/**
 * 生成C#代码的配置生成器
 */
public class CSharpConfigGenerator extends ConfigGenerator {

    {
        CSharpUtils.fillGeneratorBasicTypes(basicTypes);
        basicTypes.put("date", "DateTime");
        basicTypes.put("time", "DateTime");
        basicTypes.put("datetime", "DateTime");

        CSharpUtils.fillGeneratorClassTypes(classTypes);
        classTypes.put("date", "DateTime");
        classTypes.put("time", "DateTime");
        classTypes.put("datetime", "DateTime");

        CSharpUtils.fillGeneratorClassNames(classNames);
        classNames.put("DateTime", "System.DateTime");
        classNames.put("ImmutableSet", "System.Collections.Immutable.ImmutableSet");
        classNames.put("ImmutableList", "System.Collections.Immutable.ImmutableList");
        classNames.put("ImmutableDictionary", "System.Collections.Immutable.ImmutableDictionary");

        classNames.put("Bean", "Quan.Config.Bean");
        classNames.put("ConfigBase", "Quan.Config.ConfigBase");
        classNames.put("JObject", "Newtonsoft.Json.Linq.JObject");
        classNames.put("JArray", "Newtonsoft.Json.Linq.JArray");
    }

    //csproj文件
    private String projFile;

    public CSharpConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.cs;
    }

    @Override
    protected void parseOptions(Properties options) {
        super.parseOptions(options);
        if (enable) {
            projFile = options.getProperty(optionPrefix(true) + "projFile");
        }
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        super.prepareBean(beanDefinition);
        if (beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("System.Collections.Generic");
            beanDefinition.addImport("System.Collections.Immutable");
        }
        if (beanDefinition.getParent() == null || beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("Quan.Config");
        }
        beanDefinition.addImport("Newtonsoft.Json.Linq");
        beanDefinition.addImport("Quan.Utils");
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        ClassDefinition classDefinition = fieldDefinition.getOwner();

        if (fieldDefinition.isCollectionType() || fieldDefinition.isSimpleRef() && fieldDefinition.getRefIndex().isNormal()) {
            classDefinition.addImport("System.Collections.Generic");
        }

        if (fieldDefinition.isTimeType()) {
            if (!(classDefinition instanceof BeanDefinition) || ((BeanDefinition) classDefinition).getSelfFields().contains(fieldDefinition)) {
                classDefinition.addImport("System");
            }
        }
    }

    @Override
    protected void writeRecords() {
        super.writeRecords();
        CSharpUtils.updateProjFile(codePath, projFile, addClasses, deleteClasses);
    }

}
