package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Language;

import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    public CSharpMessageGenerator(List<String> definitionPaths, String codePath) throws Exception {
        super(definitionPaths, codePath);

        basicTypes.put("bool", "bool");
        basicTypes.put("string", "string");
        basicTypes.put("set", "HashSet");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Dictionary");

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
    }

    public CSharpMessageGenerator(String definitionPath, String codePath) throws Exception {
        this(Collections.singletonList(definitionPath), codePath);
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
        ClassDefinition fieldClass = ClassDefinition.getClass(fieldDefinition.getType());
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

        CSharpMessageGenerator messageGenerator = new CSharpMessageGenerator(definitionPath, destPath);
        messageGenerator.setPackagePrefix(packagePrefix);
        messageGenerator.generate();

    }
}
