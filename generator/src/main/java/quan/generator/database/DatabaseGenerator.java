package quan.generator.database;

import freemarker.template.Template;
import quan.database.Data;
import quan.database.ListField;
import quan.database.MapField;
import quan.database.SetField;
import quan.generator.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("byte", "byte");
        BASIC_TYPES.put("bool", "boolean");
        BASIC_TYPES.put("short", "short");
        BASIC_TYPES.put("int", "int");
        BASIC_TYPES.put("long", "long");
        BASIC_TYPES.put("float", "float");
        BASIC_TYPES.put("double", "double");
        BASIC_TYPES.put("string", "String");
        BASIC_TYPES.put("set", "Set");
        BASIC_TYPES.put("list", "List");
        BASIC_TYPES.put("map", "Map");
        BASIC_TYPES.put("bytes", "byte[]");

        CLASS_TYPES.put("byte", "Byte");
        CLASS_TYPES.put("bool", "Boolean");
        CLASS_TYPES.put("short", "Short");
        CLASS_TYPES.put("int", "Integer");
        CLASS_TYPES.put("long", "Long");
        CLASS_TYPES.put("float", "Float");
        CLASS_TYPES.put("double", "Double");
        CLASS_TYPES.put("string", "String");
        CLASS_TYPES.put("set", SetField.class.getSimpleName());
        CLASS_TYPES.put("list", ListField.class.getSimpleName());
        CLASS_TYPES.put("map", MapField.class.getSimpleName());
        CLASS_TYPES.put("bytes", "byte[]");
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public DatabaseGenerator(String codePath) throws Exception {
        super(codePath);

        Template dataTemplate = freemarkerCfg.getTemplate("data.ftl");
        templates.put(DataDefinition.class, dataTemplate);
        templates.put(BeanDefinition.class, dataTemplate);

    }

    @Override
    public final DefinitionCategory category() {
        return DefinitionCategory.data;
    }

    @Override
    protected Language supportLanguage() {
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
    protected void processClassSelf(ClassDefinition classDefinition) {
        super.processClassSelf(classDefinition);
        if (!(classDefinition instanceof BeanDefinition)) {
            return;
        }

        BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
        beanDefinition.getImports().add(Data.class.getPackage().getName() + ".*");
    }

    @Override
    protected void processBeanField(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        super.processBeanField(beanDefinition, fieldDefinition);
        if (beanDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) beanDefinition;
            if (fieldDefinition.getName().equals(dataDefinition.getKeyName())) {
                dataDefinition.setKeyType(classTypes.get(fieldDefinition.getType()));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        String definitionPath = "generator\\src\\test\\java\\quan\\generator\\database";
        String codePath = "database\\src\\test\\java";
        String packagePrefix = "quan.database";

        DatabaseGenerator generator = new DatabaseGenerator(codePath);
        generator.useXmlDefinitionParser(definitionPath, packagePrefix);
        generator.generate();
    }

}
