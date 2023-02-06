package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.parser.DefinitionParser;

import java.util.Properties;

/**
 * 生成Java代码的消息生成器
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

        classNames.put("Boolean", "java.lang.Boolean");
        classNames.put("Short", "java.lang.Short");
        classNames.put("Integer", "java.lang.Integer");
        classNames.put("Long", "java.lang.Long");
        classNames.put("Float", "java.lang.Float");
        classNames.put("Double", "java.lang.Double");
        classNames.put("String", "java.lang.String");
        classNames.put("Override", "java.lang.Override");

        classNames.put("Set", "java.util.Set");
        classNames.put("HashSet", "java.util.HashSet");
        classNames.put("List", "java.util.List");
        classNames.put("ArrayList", "java.util.ArrayList");
        classNames.put("Map", "java.util.Map");
        classNames.put("HashMap", "java.util.HashMap");
        classNames.put("Objects", "java.util.Objects");
        classNames.put("Arrays", "java.util.Arrays");

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
