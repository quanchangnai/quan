package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;

import java.util.Properties;

/**
 * 生成C#代码的消息生成器
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

        classNames.put("Array", "System.Array");
        classNames.put("HashSet", "System.Collections.Generic.HashSet");
        classNames.put("List", "System.Collections.Generic.List");
        classNames.put("Dictionary", "System.Collections.Generic.Dictionary");
        classNames.put("NullReferenceException", "System.NullReferenceException");

        classNames.put("Bean", "Quan.Message.Bean");
        classNames.put("MessageBase", "Quan.Message.MessageBase");
        classNames.put("CodedBuffer", "Quan.Message.CodedBuffer");

    }

    public CSharpMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.cs;
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

}
