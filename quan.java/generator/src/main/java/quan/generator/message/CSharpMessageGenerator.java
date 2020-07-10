package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;
import quan.definition.message.HeadDefinition;

import java.util.Properties;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    {
        basicTypes.put("bool", "bool");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "string");
        basicTypes.put("set", "HashSet");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Dictionary");
        basicTypes.put("bytes", "byte[]");

        classTypes.put("bool", "bool");
        classTypes.put("short", "short");
        classTypes.put("int", "int");
        classTypes.put("long", "long");
        classTypes.put("float", "float");
        classTypes.put("double", "double");
        classTypes.put("string", "string");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "List");
        classTypes.put("map", "Dictionary");
        classTypes.put("bytes", "byte[]");
    }

    public CSharpMessageGenerator() {
    }

    public CSharpMessageGenerator(Properties options) {
        super(options);
    }

    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    @Override
    protected void prepareBean(BeanDefinition beanDefinition) {
        super.prepareBean(beanDefinition);
        if (beanDefinition instanceof MessageDefinition) {
            HeadDefinition headDefinition = ((MessageDefinition) beanDefinition).getHead();
            if (headDefinition != null && !headDefinition.getFullPackageName(supportLanguage()).equals(beanDefinition.getFullPackageName(supportLanguage()))) {
                beanDefinition.getImports().add(headDefinition.getFullPackageName(supportLanguage()));
            }
        }
    }

    @Override
    protected void prepareFieldImports(FieldDefinition fieldDefinition) {
        super.prepareFieldImports(fieldDefinition);
        if (fieldDefinition.isTimeType()) {
            fieldDefinition.getOwner().getImports().add("System");
        }
    }
}
