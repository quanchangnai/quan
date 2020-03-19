package quan.generator.message;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.message.MessageDefinition;
import quan.definition.message.MessageHeadDefinition;
import quan.generator.util.CSharpUtils;

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

    public CSharpMessageGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    @Override
    protected void processClass(ClassDefinition classDefinition) {
        if (classDefinition instanceof MessageDefinition) {
            MessageHeadDefinition messageHeadDefinition = ((MessageDefinition) classDefinition).getHead();
            if (messageHeadDefinition != null && !messageHeadDefinition.getFullPackageName(supportLanguage()).equals(classDefinition.getFullPackageName(supportLanguage()))) {
                classDefinition.getImports().add(messageHeadDefinition.getFullPackageName(supportLanguage()));
            }
        }
        super.processClass(classDefinition);
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        CSharpUtils.processBeanFieldImports(beanDefinition, fieldDefinition);
    }

}
