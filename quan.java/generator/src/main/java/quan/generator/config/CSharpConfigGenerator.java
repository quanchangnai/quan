package quan.generator.config;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;

import java.util.Properties;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class CSharpConfigGenerator extends ConfigGenerator {

    {
        basicTypes.put("bool", "bool");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "string");
        basicTypes.put("set", "ISet");
        basicTypes.put("list", "IList");
        basicTypes.put("map", "IDictionary");
        basicTypes.put("date", "DateTime");
        basicTypes.put("time", "DateTime");
        basicTypes.put("datetime", "DateTime");

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
        classTypes.put("date", "DateTime");
        classTypes.put("time", "DateTime");
        classTypes.put("datetime", "DateTime");
    }

    public CSharpConfigGenerator() {
        super();
    }

    public CSharpConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.cs;
    }

    @Override
    protected void prepareClass(ClassDefinition classDefinition) {
        super.prepareClass(classDefinition);
        if (classDefinition instanceof ConfigDefinition) {
            classDefinition.getImports().put("System.Collections.Generic", null);
            classDefinition.getImports().put("System.Collections.Immutable", null);
        }
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        ClassDefinition classDefinition = fieldDefinition.getOwner();

        if (fieldDefinition.isCollectionType() || fieldDefinition.isSimpleRef() && classDefinition instanceof BeanDefinition) {
            classDefinition.getImports().put("System.Collections.Generic", null);
        }

        if (fieldDefinition.isTimeType()) {
            if (!(classDefinition instanceof BeanDefinition) || ((BeanDefinition) classDefinition).getSelfFields().contains(fieldDefinition)) {
                classDefinition.getImports().put("System", null);
            }
        }


    }

}
