package quan.generator.database;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;
import quan.database.Bean;
import quan.database.Cache;
import quan.database.Data;
import quan.database.Database;
import quan.database.Field;
import quan.database.ListField;
import quan.database.MapField;
import quan.database.SetField;
import quan.generator.BeanDefinition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class DatabaseGenerator extends Generator {

    public static void main(String[] args) throws Exception {

//        String srcPath = "generator\\src\\test\\java\\quan\\generator\\database";
//        String destPath = "generator\\src\\test\\java";
//        String packagePrefix = "quan.generator.database";

        String srcPath = "database\\src\\test\\java\\quan\\database";
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

    protected void processBean(BeanDefinition beanDefinition) {
        super.processBean(beanDefinition);

        if (beanDefinition instanceof DataDefinition) {
            DataDefinition dataDefinition = (DataDefinition) beanDefinition;
            dataDefinition.setKeyType(classTypes.get(dataDefinition.getKeyType()));
            beanDefinition.getImports().add(Data.class.getName());
        } else {
            beanDefinition.getImports().add(Bean.class.getName());
        }

        beanDefinition.getImports().add(Field.class.getPackage().getName() + ".*");
        beanDefinition.getImports().add("java.util.*");
        beanDefinition.getImports().add(Database.class.getName());
        beanDefinition.getImports().add(Cache.class.getName());
        beanDefinition.getImports().add(Data.class.getName());
        beanDefinition.getImports().add(JSONObject.class.getName());
        beanDefinition.getImports().add(JSONArray.class.getName());
        beanDefinition.getImports().add(PMap.class.getName());
        beanDefinition.getImports().add(PSet.class.getName());
        beanDefinition.getImports().add(PVector.class.getName());
        beanDefinition.getImports().add(Empty.class.getName());

    }

}
