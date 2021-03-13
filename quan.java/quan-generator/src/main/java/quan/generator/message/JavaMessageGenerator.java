package quan.generator.message;

import quan.definition.FieldDefinition;
import quan.definition.Language;

import java.util.Properties;

/**
 * Created by quanchangnai on 2017/7/6.
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
    }

    public JavaMessageGenerator() {
    }

    public JavaMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.java;
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        if (fieldDefinition.isBytesType() || fieldDefinition.isStringType()
                || fieldDefinition.isCollectionType() || fieldDefinition.isTimeType()
                || fieldDefinition.isBeanType() && !fieldDefinition.isOptional()) {
            fieldDefinition.getOwner().getImports().put("java.util.*", null);
        }
    }

}
