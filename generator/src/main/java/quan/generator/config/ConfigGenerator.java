package quan.generator.config;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigGenerator extends Generator {


    public ConfigGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);

        Template configTemplate = freemarkerCfg.getTemplate("config." + getLanguage() + ".ftl");

        templates.put(ConfigDefinition.class, configTemplate);
        templates.put(BeanDefinition.class, configTemplate);

    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof ConfigDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

    @Override
    protected void processClassDependency(ClassDefinition classDefinition) {
        super.processClassDependency(classDefinition);

        if (!(classDefinition instanceof ConfigDefinition)) {
            return;
        }

        ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
        if (configDefinition.getParent() != null) {
            ClassDefinition parentClassDefinition = ConfigDefinition.getAll().get(configDefinition.getParent());
            if (!parentClassDefinition.getPackageName().equals(configDefinition.getPackageName())) {
                configDefinition.getImports().add(parentClassDefinition.getFullName());
            }
        }

    }
}
