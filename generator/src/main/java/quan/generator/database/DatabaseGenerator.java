package quan.generator.database;

import freemarker.template.Template;
import quan.database.Data;
import quan.database.ListField;
import quan.database.MapField;
import quan.database.SetField;
import quan.generator.*;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public DatabaseGenerator(String codePath) throws Exception {
        super(codePath);

        basicTypes.put("byte", "byte");
        classTypes.put("byte", "Byte");
        classTypes.put("set", SetField.class.getSimpleName());
        classTypes.put("list", ListField.class.getSimpleName());
        classTypes.put("map", MapField.class.getSimpleName());

        Template dataTemplate = freemarkerCfg.getTemplate("data.ftl");

        templates.put(DataDefinition.class, dataTemplate);
        templates.put(BeanDefinition.class, dataTemplate);

    }

    @Override
    public Definition.Category category() {
        return Definition.Category.data;
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
