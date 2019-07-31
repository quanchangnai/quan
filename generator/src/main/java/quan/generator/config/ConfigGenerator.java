package quan.generator.config;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Generator;

import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigGenerator extends Generator {

    public ConfigGenerator(List<String> definitionPaths, String codePath) throws Exception {
        super(definitionPaths, codePath);

        Template configTemplate = freemarkerCfg.getTemplate("config." + supportLanguage() + ".ftl");

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
        if (!(classDefinition instanceof ConfigDefinition)) {
            super.processClassDependency(classDefinition);
            return;
        }

        ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
        for (FieldDefinition fieldDefinition : configDefinition.getSelfFields()) {
            processField(classDefinition, fieldDefinition);
        }
        ConfigDefinition parentDefinition = configDefinition.getParentDefinition();
        if (parentDefinition != null && !parentDefinition.getPackageName().equals(configDefinition.getPackageName())) {
            configDefinition.getImports().add(parentDefinition.getFullName());
        }
    }

}
