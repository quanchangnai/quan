package quan.generator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends ClassDefinition {

    private Set<String> imports = new HashSet<>();

    @Override
    public int getDefinitionType() {
        return 2;
    }

    public Set<String> getImports() {
        return imports;
    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        //校验字段类型
        if (fieldDefinition.getType() == null || fieldDefinition.getType().trim().equals("")) {
            throwValidatedError("字段[" + fieldDefinition.getName() + "]类型不能为空");
        }

        if (fieldDefinition.isCollectionType()) {
            if (fieldDefinition.getValueType() == null || fieldDefinition.getValueType().trim().equals("")) {
                throwValidatedError(fieldDefinition.getType() + "类型字段[" + fieldDefinition.getName() + "]的值类型不能为空");
            }

            if (fieldDefinition.getType().equals("map") && (fieldDefinition.getKeyType() == null || fieldDefinition.getKeyType().trim().equals(""))) {
                throwValidatedError(fieldDefinition.getType() + "类型字段[" + fieldDefinition.getName() + "]的键类型不能为空");
            }

        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name=" + getName() +
                ",imports=" + imports +
                ",packageName=" + getPackageName() +
                ",fields=" + getFields() +
                '}';
    }
}
