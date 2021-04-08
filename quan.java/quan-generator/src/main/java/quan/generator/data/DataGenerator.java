package quan.generator.data;

import freemarker.template.Template;
import quan.definition.*;
import quan.definition.data.DataDefinition;
import quan.generator.Generator;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by quanchangnai on 2017/7/6.
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
    }

    public DataGenerator() {
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
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        if (fieldDefinition.getOwner() instanceof DataDefinition) {
            DataDefinition owner = (DataDefinition) fieldDefinition.getOwner();
            if (fieldDefinition.getName().equals(owner.getIdName())) {
                owner.setIdField(fieldDefinition);
            }
        }
    }

}
