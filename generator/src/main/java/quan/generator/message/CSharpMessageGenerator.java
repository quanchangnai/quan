package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    public static final Map<String, String> BASIC_TYPES = new HashMap<>();

    public static final Map<String, String> CLASS_TYPES = new HashMap<>();

    static {
        BASIC_TYPES.put("bool", "bool");
        BASIC_TYPES.put("short", "short");
        BASIC_TYPES.put("int", "int");
        BASIC_TYPES.put("long", "long");
        BASIC_TYPES.put("float", "float");
        BASIC_TYPES.put("double", "double");
        BASIC_TYPES.put("string", "string");
        BASIC_TYPES.put("set", "HashSet");
        BASIC_TYPES.put("list", "List");
        BASIC_TYPES.put("map", "Dictionary");
        BASIC_TYPES.put("bytes", "byte[]");

        CLASS_TYPES.put("bool", "bool");
        CLASS_TYPES.put("short", "short");
        CLASS_TYPES.put("int", "int");
        CLASS_TYPES.put("long", "long");
        CLASS_TYPES.put("float", "float");
        CLASS_TYPES.put("double", "double");
        CLASS_TYPES.put("string", "string");
        CLASS_TYPES.put("set", "HashSet");
        CLASS_TYPES.put("list", "List");
        CLASS_TYPES.put("map", "Dictionary");
        CLASS_TYPES.put("bytes", "byte[]");
    }

    {
        basicTypes.putAll(BASIC_TYPES);
        classTypes.putAll(CLASS_TYPES);
    }

    public CSharpMessageGenerator(String codePath) throws Exception {
        super(codePath);
    }


    @Override
    protected Language supportLanguage() {
        return Language.cs;
    }

    protected void processClassSelf(ClassDefinition classDefinition) {
        String packageName = classDefinition.getPackageName();
        //C#命名空间首字母大写
        StringBuilder newPackageName = new StringBuilder();
        for (int i = 0; i < packageName.length(); i++) {
            String c = String.valueOf(packageName.charAt(i));
            if (i == 0 || packageName.charAt(i - 1) == '.') {
                c = c.toUpperCase();
            }
            newPackageName.append(c);
        }
        classDefinition.setPackageName(newPackageName.toString());

        super.processClassSelf(classDefinition);
    }

    @Override
    protected void processBeanFieldImports(BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        ClassDefinition fieldClass = definitionParser.getClass(fieldDefinition.getType());
        if (fieldClass != null && !fieldClass.getPackageName().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldClass.getPackageName());
        }
        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getPackageName().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldValueBean.getPackageName());
        }
    }

    public static void main(String[] args) throws Exception {

        String definitionPath = "generator\\src\\test\\java\\quan\\generator\\message";
        String destPath = "message";
        String packagePrefix = "MessageCS.test";

        CSharpMessageGenerator messageGenerator = new CSharpMessageGenerator(destPath);
        messageGenerator.useXmlDefinitionParser(definitionPath, packagePrefix);
        messageGenerator.generate();

    }
}
