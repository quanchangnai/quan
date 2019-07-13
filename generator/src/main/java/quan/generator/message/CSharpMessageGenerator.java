package quan.generator.message;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.EnumDefinition;
import quan.generator.FieldDefinition;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class CSharpMessageGenerator extends MessageGenerator {

    public CSharpMessageGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);
        basicTypes.put("bytes", "byte[]");
        basicTypes.put("bool", "bool");
        basicTypes.put("string", "string");
        basicTypes.put("set", "HashSet");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Dictionary");

        classTypes.put("bytes", "byte[]");
        classTypes.put("bool", "bool");
        classTypes.put("int", "int");
        classTypes.put("string", "string");
        classTypes.put("byte", "byte");
        classTypes.put("short", "short");
        classTypes.put("long", "long");
        classTypes.put("float", "float");
        classTypes.put("double", "double");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "List");
        classTypes.put("map", "Dictionary");

    }

    @Override
    protected String getLanguage() {
        return "cs";
    }

    @Override
    protected void processBeanImports(FieldDefinition fieldDefinition) {
        BeanDefinition beanDefinition = (BeanDefinition) fieldDefinition.getClassDefinition();

        ClassDefinition fieldTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getType());
        if (fieldTypeClassDefinition != null) {
            if (!fieldTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
                beanDefinition.getImports().add(fieldTypeClassDefinition.getPackageName());
            }
            if (fieldTypeClassDefinition instanceof EnumDefinition) {
                fieldDefinition.setEnumType(true);
            }
        } else if (fieldDefinition.isTypeWithPackage() && !fieldDefinition.getTypeWithPackage().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldDefinition.getTypePackage());
        }

        ClassDefinition fieldValueTypeClassDefinition = ClassDefinition.getAll().get(fieldDefinition.getValueType());
        if (fieldValueTypeClassDefinition != null && !fieldValueTypeClassDefinition.getPackageName().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldValueTypeClassDefinition.getPackageName());
        }
        if (fieldValueTypeClassDefinition == null && fieldDefinition.isValueTypeWithPackage() && !fieldDefinition.getValueTypePackage().equals(beanDefinition.getPackageName())) {
            beanDefinition.getImports().add(fieldDefinition.getValueTypePackage());
        }
    }
}
