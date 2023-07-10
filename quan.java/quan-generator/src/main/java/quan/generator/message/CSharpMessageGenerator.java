package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.generator.util.CSharpUtils;

import java.util.Properties;

/**
 * 生成C#代码的消息生成器
 */
public class CSharpMessageGenerator extends MessageGenerator {

    {
        CSharpUtils.fillGeneratorBasicTypes(basicTypes);
        basicTypes.put("bytes", "byte[]");

        CSharpUtils.fillGeneratorClassTypes(classTypes);
        classTypes.put("bytes", "byte[]");

        CSharpUtils.fillGeneratorClassNames(classNames);
        classNames.put("Bean", "Quan.Message.Bean");
        classNames.put("MessageBase", "Quan.Message.MessageBase");
        classNames.put("CodedBuffer", "Quan.Message.CodedBuffer");

    }

    //csproj文件
    private String projFile;

    public CSharpMessageGenerator(Properties options) {
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
        beanDefinition.addImport("Quan.Message");
        beanDefinition.addImport("Quan.Utils");
        super.prepareBean(beanDefinition);
    }

    @Override
    protected void prepareField(FieldDefinition field) {
        super.prepareField(field);
        if (field.isCollectionType()) {
            field.getOwner().addImport("System.Collections.Generic");
        }
        if (field.isBytesType() || field.isStringType() || field.isTimeType() || field.isBeanType() && !field.isOptional()) {
            field.getOwner().addImport("System");
        }
    }

    @Override
    protected void writeRecords() {
        super.writeRecords();
        CSharpUtils.updateProjFile(codePath, projFile, addClasses, deleteClasses);
    }

}
