package quan.generator.message;

import quan.definition.FieldDefinition;
import quan.definition.Language;

import java.util.Properties;

/**
 * Created by quanchangnai on 2017/7/6.
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
    }

    public CSharpMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.cs;
    }

    @Override
    protected void prepareField(FieldDefinition field) {
        super.prepareField(field);
        if (field.isCollectionType()) {
            field.getOwner().getImports().put("System.Collections.Generic", null);
        }
        if (field.isBytesType() || field.isStringType() || field.isTimeType() || field.isBeanType() && !field.isOptional()) {
            field.getOwner().getImports().put("System", null);
        }
    }

}
