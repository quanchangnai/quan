package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.parser.DefinitionParser;
import quan.generator.util.JavaUtils;

import java.util.Properties;

/**
 * 生成Java代码的消息生成器
 */
public class JavaMessageGenerator extends MessageGenerator {

    {
        JavaUtils.fillGeneratorBasicTypes(basicTypes);
        basicTypes.put("bytes", "byte[]");

        JavaUtils.fillGeneratorClassTypes(classTypes);
        classTypes.put("bytes", "byte[]");

        JavaUtils.fillGeneratorClassNames(classNames);
        classNames.put("Bean", "quan.message.Bean");
        classNames.put("Message", "quan.message.Message");
        classNames.put("CodedBuffer", "quan.message.CodedBuffer");
        classNames.put("NumberUtils", "quan.util.NumberUtils");
    }

    public JavaMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.java;
    }


    @Override
    public DefinitionParser getParser() {
        return super.getParser();
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        beanDefinition.addImport("quan.message.*");
        super.prepareBean(beanDefinition);
    }


    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        if (fieldDefinition.isBytesType() || fieldDefinition.isStringType()
                || fieldDefinition.isCollectionType() || fieldDefinition.isTimeType()
                || fieldDefinition.isBeanType() && !fieldDefinition.isOptional()) {
            fieldDefinition.getOwner().addImport("java.util.*");

        }
        if (fieldDefinition.getMin() != null || fieldDefinition.getMax() != null) {
            fieldDefinition.getOwner().addImport("quan.util.NumberUtils");
        }
    }

}
