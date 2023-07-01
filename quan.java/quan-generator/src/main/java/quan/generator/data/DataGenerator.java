package quan.generator.data;

import freemarker.template.Template;
import quan.definition.*;
import quan.definition.data.DataDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.Properties;

/**
 * 数据生成器
 */
public class DataGenerator extends Generator {

    {
        basicTypes.put("byte", "byte");
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

        classTypes.put("byte", "Byte");
        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "SetField");
        classTypes.put("list", "ListField");
        classTypes.put("map", "MapField");

        classNames.put("Boolean", "java.util.Boolean");
        classNames.put("Short", "java.util.Short");
        classNames.put("Integer", "java.util.Integer");
        classNames.put("Long", "java.util.Long");
        classNames.put("Float", "java.util.Float");
        classNames.put("Double", "java.util.Double");
        classNames.put("String", "java.util.String");
        classNames.put("Set", "java.util.Set");
        classNames.put("HashSet", "java.util.HashSet");
        classNames.put("List", "java.util.List");
        classNames.put("ArrayList", "java.util.ArrayList");
        classNames.put("Map", "java.util.Map");
        classNames.put("HashMap", "java.util.HashMap");
        classNames.put("Collection", "java.util.Collection");
        classNames.put("Objects", "java.util.Objects");
        classNames.put("Class", "java.lang.Class");

        classNames.put("NumberUtils", "quan.util.NumberUtils");
        classNames.put("Index", "java.data.Index");
        classNames.put("NodeBean", "java.data.NodeBean");
        classNames.put("Data", "java.data.Data");
        classNames.put("Transaction", "java.data.Transaction");
        classNames.put("IntField", "java.data.field.IntField");
        classNames.put("BeanField", "java.data.field.BeanField");
        classNames.put("BoolField", "java.data.field.BoolField");
        classNames.put("DoubleField", "java.data.field.DoubleField");
        classNames.put("FloatField", "java.data.field.FloatField");
        classNames.put("ListField", "java.data.field.ListField");
        classNames.put("LongField", "java.data.field.LongField");
        classNames.put("MapField", "java.data.field.MapField");
        classNames.put("SetField", "java.data.field.SetField");
        classNames.put("ShortField", "java.data.field.ShortField");
        classNames.put("StringField", "java.data.field.StringField");
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
        if (fieldDefinition.getOwner() instanceof DataDefinition) {
            DataDefinition owner = (DataDefinition) fieldDefinition.getOwner();
            if (fieldDefinition.getName().equals(owner.getIdName())) {
                owner.setIdField(fieldDefinition);
            }
        }
        if (fieldDefinition.getMin() != null || fieldDefinition.getMax() != null) {
            fieldDefinition.getOwner().addImport("quan.util.NumberUtils");
        }
    }

}
