package quan.generator.data;

import freemarker.template.Template;
import quan.definition.*;
import quan.definition.data.DataDefinition;
import quan.generator.Generator;
import quan.generator.util.JavaUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 数据生成器
 */
public class DataGenerator extends Generator {

    {
        JavaUtils.fillGeneratorBasicTypes(basicTypes);

        classTypes.put("byte", "Byte");
        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");

        JavaUtils.fillGeneratorClassTypes(classTypes);

        classTypes.put("set", "SetField");
        classTypes.put("list", "ListField");
        classTypes.put("map", "MapField");


        JavaUtils.fillGeneratorClassNames(classNames);
        classNames.put("Index", "quan.data.Index");
        classNames.put("Bean", "quan.data.Bean");
        classNames.put("Data", "quan.data.Data");
        classNames.put("Transaction", "quan.data.Transaction");
        classNames.put("BaseField", "quan.data.field.BaseField");
        classNames.put("BeanField", "quan.data.field.BeanField");
        classNames.put("ListField", "quan.data.field.ListField");
        classNames.put("MapField", "quan.data.field.MapField");
        classNames.put("SetField", "quan.data.field.SetField");
        classNames.put("NumberUtils", "quan.util.NumberUtils");
        classNames.put("JsonStringWriter", "quan.data.bson.JsonStringWriter");
        classNames.put("Codec", "org.bson.codecs.Codec");
        classNames.put("BsonType", "org.bson.codecs.BsonType");
        classNames.put("BsonReader", "org.bson.BsonReader");
        classNames.put("BsonWriter", "org.bson.BsonWriter");
        classNames.put("EncoderContext", "org.bson.codecs.EncoderContext");
        classNames.put("DecoderContext", "org.bson.codecs.DecoderContext");
        classNames.put("CodecRegistry", "org.bson.codecs.configuration.CodecRegistry");
    }

    public DataGenerator(Properties options) {
        super(options);
    }

    @Override
    public final Category category() {
        return Category.data;
    }

    @Override
    protected Language language() {
        return Language.java;
    }


    @Override
    protected void parseOptions(Properties options) {
        super.parseOptions(options);

        if (enable && parser != null) {
            parser.setDataNamePattern(options.getProperty(optionPrefix(false) + "namePattern"));
        }
    }

    @Override
    protected String optionPrefix(boolean useLanguage) {
        return category() + ".";
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof DataDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void initFreemarker() {
        super.initFreemarker();
        try {
            Template dataTemplate = freemarkerCfg.getTemplate("data.ftl");
            templates.put(DataDefinition.class, dataTemplate);
            templates.put(BeanDefinition.class, dataTemplate);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        beanDefinition.addImport("java.util.*");
        beanDefinition.addImport("org.bson.BsonType");
        beanDefinition.addImport("org.bson.BsonReader");
        beanDefinition.addImport("org.bson.BsonWriter");
        beanDefinition.addImport("org.bson.codecs.Codec");
        beanDefinition.addImport("org.bson.codecs.EncoderContext");
        beanDefinition.addImport("org.bson.codecs.DecoderContext");
        beanDefinition.addImport("org.bson.codecs.configuration.CodecRegistry");
        beanDefinition.addImport("quan.data.*");
        beanDefinition.addImport("quan.data.field.*");
        if (beanDefinition instanceof DataDefinition) {
            beanDefinition.addImport("quan.data.bson.JsonStringWriter");
        }

        super.prepareBean(beanDefinition);
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);

        if (fieldDefinition.getMin() != null || fieldDefinition.getMax() != null) {
            fieldDefinition.getOwner().addImport("quan.util.NumberUtils");
        }
    }

}
