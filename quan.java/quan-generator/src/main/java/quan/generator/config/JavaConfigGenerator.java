package quan.generator.config;

import quan.definition.BeanDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;
import quan.generator.util.JavaUtils;

import java.util.Date;
import java.util.Properties;

/**
 * 生成Java代码的配置生成器
 */
public class JavaConfigGenerator extends ConfigGenerator {

    {
        JavaUtils.fillGeneratorBasicTypes(basicTypes);
        basicTypes.put("date", "Date");
        basicTypes.put("time", "Date");
        basicTypes.put("datetime", "Date");

        JavaUtils.fillGeneratorClassTypes(classTypes);
        classTypes.put("date", "Date");
        classTypes.put("time", "Date");
        classTypes.put("datetime", "Date");

        JavaUtils.fillGeneratorClassNames(classNames);
        classNames.put("Date", Date.class.getName());
        classNames.put("Bean", "quan.config.Bean");
        classNames.put("Config", "quan.config.Config");
        classNames.put("ConfigLoader", "quan.config.load.ConfigLoader");
        classNames.put("JSONObject", "com.alibaba.fastjson.JSONObject");
        classNames.put("JSONArray", "com.alibaba.fastjson.JSONArray");
    }

    public JavaConfigGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language language() {
        return Language.java;
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("java.util.*");
            beanDefinition.addImport("quan.config.load.ConfigLoader");
        }
        if (beanDefinition.getParent() == null || beanDefinition instanceof ConfigDefinition) {
            beanDefinition.addImport("quan.config.*");
        }
        beanDefinition.addImport("com.alibaba.fastjson.*");

        super.prepareBean(beanDefinition);
    }

    @Override
    protected void prepareField(FieldDefinition fieldDefinition) {
        super.prepareField(fieldDefinition);
        if (fieldDefinition.isCollectionType() || fieldDefinition.isSimpleRef() && fieldDefinition.getRefIndex().isNormal()) {
            fieldDefinition.getOwner().addImport("java.util.*");
        }
    }

}
