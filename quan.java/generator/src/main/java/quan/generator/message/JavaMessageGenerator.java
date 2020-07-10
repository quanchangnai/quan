package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;
import quan.definition.message.HeadDefinition;

import java.util.Properties;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class JavaMessageGenerator extends MessageGenerator {

    {
        basicTypes.put("bool", "boolean");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "String");
        basicTypes.put("set", "Set");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Map");
        basicTypes.put("bytes", "byte[]");

        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "ArrayList");
        classTypes.put("map", "HashMap");
        classTypes.put("bytes", "byte[]");
    }

    public JavaMessageGenerator() {
    }

    public JavaMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        super.prepareBean(beanDefinition);
        if (beanDefinition instanceof MessageDefinition) {
            HeadDefinition headDefinition = ((MessageDefinition) beanDefinition).getHead();
            if (headDefinition != null && !headDefinition.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
                beanDefinition.getImports().add(headDefinition.getFullName(supportLanguage()));
            }
        }
    }
}
