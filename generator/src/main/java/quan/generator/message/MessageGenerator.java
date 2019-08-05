package quan.generator.message;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Definition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    public MessageGenerator(String codePath) throws Exception {
        super(codePath);
        basicTypes.put("bytes", "byte[]");
        classTypes.put("bytes", "byte[]");

        Template messageTemplate = freemarkerCfg.getTemplate("message." + supportLanguage() + ".ftl");

        templates.put(MessageDefinition.class, messageTemplate);
        templates.put(BeanDefinition.class, messageTemplate);
    }

    @Override
    public Definition.Category category() {
        return Definition.Category.data;
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof MessageDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

}
