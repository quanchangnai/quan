package quan.generator.message;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Generator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    public MessageGenerator(List<String> srcPaths, String destPath) throws Exception {
        super(srcPaths, destPath);
        basicTypes.put("bytes", "byte[]");
        classTypes.put("bytes", "byte[]");

        Template messageTemplate = freemarkerCfg.getTemplate("message." + supportLanguage() + ".ftl");

        templates.put(MessageDefinition.class, messageTemplate);
        templates.put(BeanDefinition.class, messageTemplate);
    }

    @Override
    protected boolean support(ClassDefinition classDefinition) {
        if (classDefinition instanceof MessageDefinition) {
            return true;
        }
        return super.support(classDefinition);
    }

}
