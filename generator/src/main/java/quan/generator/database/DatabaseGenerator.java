package quan.generator.database;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.pcollections.PMap;
import quan.database.*;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public static void main(String[] args) throws Exception {

        String srcPath = "generator\\src\\test\\java\\quan\\generator\\database";

//        String destPath = "generator\\src\\test\\java";
//        String packagePrefix = "quan.generator.database";

        String destPath = "database\\src\\test\\java";
        String packagePrefix = "quan.database";

        DatabaseGenerator generator = new DatabaseGenerator(srcPath, destPath);
        generator.setPackagePrefix(packagePrefix);
        generator.generate();
    }


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

    protected void processBean(BeanDefinition beanDefinition) {
        super.processBean(beanDefinition);

        if (beanDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) beanDefinition;
            dataDefinition.setKeyType(classTypes.get(dataDefinition.getKeyType()));
        }

        beanDefinition.getImports().add("java.util.*");
        beanDefinition.getImports().add(Data.class.getPackage().getName() + ".*");
        beanDefinition.getImports().add(JSONObject.class.getPackage().getName() + ".*");
        beanDefinition.getImports().add(PMap.class.getPackage().getName() + ".*");

    }

}
