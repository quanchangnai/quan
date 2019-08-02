package quan.generator;

import quan.generator.config.ConfigDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends ClassDefinition {

    private Set<String> imports = new HashSet<>();

    //配置Bean的字段分隔符
    private String delimiter;

    public BeanDefinition() {
    }

    public BeanDefinition(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public int getDefinitionType() {
        return 2;
    }

    public Set<String> getImports() {
        return imports;
    }


    public static boolean isBeanDefinition(String type) {
        return ClassDefinition.getAll().get(type) instanceof BeanDefinition;
    }

    @Override
    public void validate() {
        super.validate();
        validateDelimiter();
    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        //校验字段类型
        if (fieldDefinition.getType() == null) {
            addValidatedError(getName4Validate("的") + "字段[" + fieldDefinition.getName() + "]类型不能为空");
            return;
        }

        if (!fieldDefinition.isLegalType()) {
            addValidatedError(getName4Validate("的") + "字段" + fieldDefinition.getName4Validate() + "类型[" + fieldDefinition.getType() + "]不合法");
            return;
        }

        //校验集合值类型
        if (!fieldDefinition.isCollectionType()) {
            return;
        }
        if (fieldDefinition.getValueType() == null) {
            addValidatedError(getName4Validate("的") + fieldDefinition.getType() + "类型字段" + fieldDefinition.getName4Validate() + "的值类型不能为空");
            return;
        }
        if (!fieldDefinition.isLegalValueType()) {
            addValidatedError(getName4Validate("的") + fieldDefinition.getType() + "类型字段" + fieldDefinition.getName4Validate() + "的值类型[" + fieldDefinition.getValueType() + "]不合法");
            return;
        }
        if (fieldDefinition.getType().equals("map")) {
            //校验map键类型
            if (fieldDefinition.getKeyType() == null) {
                addValidatedError(getName4Validate("的") + fieldDefinition.getType() + "类型字段" + fieldDefinition.getName4Validate() + "的键类型不能为空");
                return;
            }
            if (!fieldDefinition.isPrimitiveKeyType()) {
                addValidatedError(getName4Validate("的") + fieldDefinition.getType() + "类型字段" + fieldDefinition.getName4Validate() + "的键类型[" + fieldDefinition.getKeyType() + "]不合法");
            }
        }

    }

    public String getDelimiter() {
        if (delimiter != null) {
            return delimiter;
        }
        return "_";
    }

    public BeanDefinition setDelimiter(String delimiter) {
        if (delimiter == null || delimiter.trim().equals("")) {
            return this;
        }
        this.delimiter = delimiter;
        return this;
    }

    public String getEscapedDelimiter() {
        return ConfigDefinition.escapeDelimiter(getDelimiter());
    }

    private void validateDelimiter() {
        if (getClass() != BeanDefinition.class) {
            return;
        }
        String delimiter = getDelimiter();
        if (delimiter.length() != 1) {
            addValidatedError("类" + getName4Validate() + "的分隔符[" + delimiter + "]长度必须1个字符");
        }
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (!ConfigDefinition.allowDelimiters.contains(s)) {
                addValidatedError("类" + getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + ConfigDefinition.allowDelimiters);
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
