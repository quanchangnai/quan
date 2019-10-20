package quan.generator.util;

import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.Language;

/**
 * C#工具类
 * Created by quanchangnai on 2019/8/16.
 */
public class CSharpUtils {

    /**
     * C#命名空间首字母大写驼峰格式
     *
     * @param packageName
     * @return
     */
    public static String toCapitalCamel(String packageName) {
        StringBuilder namespace = new StringBuilder();
        for (int i = 0; i < packageName.length(); i++) {
            String c = String.valueOf(packageName.charAt(i));
            if (i == 0 || packageName.charAt(i - 1) == '.') {
                c = c.toUpperCase();
            }
            namespace.append(c);
        }
        return namespace.toString();
    }

    public static void processBeanFieldImports( BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        if (fieldDefinition.isTimeType()) {
            beanDefinition.getImports().add("System");
        }
        ClassDefinition fieldClass = fieldDefinition.getClassDefinition();
        if (fieldClass != null && !fieldClass.getFullPackageName(Language.cs).equals(beanDefinition.getFullPackageName(Language.cs))) {
            beanDefinition.getImports().add(fieldClass.getFullPackageName(Language.cs));
        }
        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getFullPackageName(Language.cs).equals(beanDefinition.getFullPackageName(Language.cs))) {
            beanDefinition.getImports().add(fieldValueBean.getFullPackageName(Language.cs));
        }
    }

}
