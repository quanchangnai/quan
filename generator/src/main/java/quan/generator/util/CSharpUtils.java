package quan.generator.util;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.DefinitionParser;
import quan.generator.FieldDefinition;

/**
 * C#工具类
 * Created by quanchangnai on 2019/8/16.
 */
public class CSharpUtils {

    /**
     * C#命名空间首字母大写
     *
     * @param packageName
     * @return
     */
    public static String namespace(String packageName) {
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

    public static void processBeanFieldImports(DefinitionParser definitionParser, BeanDefinition beanDefinition, FieldDefinition fieldDefinition) {
        ClassDefinition fieldClass = definitionParser.getClass(fieldDefinition.getType());
        if (fieldClass != null && !fieldClass.getFullPackageName().equals(beanDefinition.getFullPackageName())) {
            beanDefinition.getImports().add(fieldClass.getFullPackageName());
        }
        BeanDefinition fieldValueBean = fieldDefinition.getValueBean();
        if (fieldValueBean != null && !fieldValueBean.getFullPackageName().equals(beanDefinition.getFullPackageName())) {
            beanDefinition.getImports().add(fieldValueBean.getFullPackageName());
        }
    }

}
