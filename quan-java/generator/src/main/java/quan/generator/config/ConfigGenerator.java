package quan.generator.config;

import freemarker.template.Template;
import quan.generator.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigGenerator extends Generator {

    public ConfigGenerator(String codePath) throws Exception {
        super(codePath);

        Template configTemplate = freemarkerCfg.getTemplate("config." + supportLanguage() + ".ftl");
        templates.put(ConfigDefinition.class, configTemplate);
        templates.put(BeanDefinition.class, configTemplate);
    }

    @Override
    public final DefinitionCategory category() {
        return DefinitionCategory.config;
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
        ConfigDefinition parentDefinition = configDefinition.getParentConfig();
        if (parentDefinition != null && !parentDefinition.getFullPackageName().equals(configDefinition.getFullPackageName())) {
            configDefinition.getImports().add(parentDefinition.getFullName());
        }
    }

}
