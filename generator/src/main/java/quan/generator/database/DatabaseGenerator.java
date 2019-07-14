package quan.generator.database;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.pcollections.PMap;
import quan.database.Data;
import quan.database.ListField;
import quan.database.MapField;
import quan.database.SetField;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public DatabaseGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);

        classTypes.put("set", SetField.class.getSimpleName());
        classTypes.put("list", ListField.class.getSimpleName());
        classTypes.put("map", MapField.class.getSimpleName());

        Template dataTemplate = freemarkerCfg.getTemplate("data.ftl");

        templates.put(DataDefinition.class, dataTemplate);
        templates.put(BeanDefinition.class, dataTemplate);

    }

    @Override
    protected String getLanguage() {
        return "java";
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof DataDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void processBean(BeanDefinition beanDefinition) {
        super.processBean(beanDefinition);

        beanDefinition.getImports().add("java.util.*");
        beanDefinition.getImports().add(Data.class.getPackage().getName() + ".*");
        beanDefinition.getImports().add(JSONObject.class.getPackage().getName() + ".*");
        beanDefinition.getImports().add(PMap.class.getPackage().getName() + ".*");

    }

    @Override
    protected void processBeanField(FieldDefinition fieldDefinition) {
        super.processBeanField(fieldDefinition);

        ClassDefinition classDefinition = fieldDefinition.getClassDefinition();
        if (classDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) classDefinition;
            if (fieldDefinition.getName().equals(dataDefinition.getKeyName())) {
                dataDefinition.setKeyType(classTypes.get(fieldDefinition.getType()));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        String srcPath = "generator\\src\\test\\java\\quan\\generator\\database";
        String destPath = "database\\src\\test\\java";
        String packagePrefix = "quan.database";

        DatabaseGenerator generator = new DatabaseGenerator(srcPath, destPath);
        generator.setPackagePrefix(packagePrefix);
        generator.generate();
    }

}
