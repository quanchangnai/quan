package quan.generator;

import org.apache.commons.lang3.StringUtils;
import quan.generator.config.ConfigDefinition;
import quan.generator.config.IndexDefinition;
import quan.generator.database.DataDefinition;
import quan.generator.message.MessageDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends ClassDefinition {

    private Set<String> imports = new HashSet<>();

    //配置Bean的字段分隔符
    private String delimiter = "_";

    public BeanDefinition() {
    }

    public BeanDefinition(String delimiter) {
        if (!StringUtils.isBlank(delimiter)) {
            this.delimiter = delimiter;
        }
    }

    @Override
    public int getDefinitionType() {
        return 2;
    }

    public Set<String> getImports() {
        return imports;
    }


    @Override
    public void validate() {
        super.validate();
        validateDelimiter();
    }

    @Override
    protected void validateField(FieldDefinition field) {
        super.validateField(field);

        //校验字段类型
        if (field.getType() == null) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "类型不能为空");
            return;
        }

        if (!field.isLegalType()) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "类型[" + field.getType() + "]不合法");
            return;
        }

        //校验集合值类型
        if (field.isCollectionType()) {
            validateFieldCollectionType(field);
        }

        //校验字段循环依赖，字段类型为bean类型或者集合类型字段的值类型为bean
        Set<BeanDefinition> fieldBeans = new HashSet<>();
        fieldBeans.add(this);
        boolean loop = validateFieldBeanLoop(field, field, fieldBeans);
        if (loop) {
            field.setLoop(true);
        }
    }


    protected boolean validateFieldBeanLoop(FieldDefinition rootField, FieldDefinition field, Set<BeanDefinition> fieldBeans) {
        BeanDefinition fieldBean = null;
        if (field.isBeanType()) {
            fieldBean = field.getBean();
        } else if (field.isCollectionType()) {
            fieldBean = field.getValueBean();
        }

        if (fieldBean == null) {
            return false;
        }

        if (fieldBeans.contains(fieldBean)) {
            addValidatedError(getName4Validate("的") + rootField.getName4Validate() + "循环依赖类型[" + fieldBean.getName() + "]");
            return true;
        }

        fieldBeans.add(fieldBean);

        for (FieldDefinition fieldBeanField : fieldBean.getFields()) {
            if (validateFieldBeanLoop(rootField, fieldBeanField, fieldBeans)) {
                return true;
            }
        }

        return false;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public BeanDefinition setDelimiter(String delimiter) {
        if (StringUtils.isBlank(delimiter)) {
            return this;
        }
        this.delimiter = delimiter.trim();
        return this;
    }

    public String getEscapedDelimiter() {
        return ConfigDefinition.escapeDelimiter(getDelimiter());
    }

    private void validateDelimiter() {
        if (getClass() != BeanDefinition.class) {
            return;
        }
        if (delimiter.length() != 1) {
            addValidatedError(getName4Validate() + "的分隔符[" + delimiter + "]长度必须1个字符");
        }
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (!ConfigDefinition.allowDelimiters.contains(s)) {
                addValidatedError(getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + ConfigDefinition.allowDelimiters);
            }
        }
    }


    protected void validateFieldCollectionType(FieldDefinition field) {
        if (field.getValueType() == null) {
            addValidatedError(getName4Validate("的") + field.getType() + "类型" + field.getName4Validate() + "的值类型不能为空");
            return;
        }
        if (!field.isLegalValueType()) {
            addValidatedError(getName4Validate("的") + field.getType() + "类型" + field.getName4Validate() + "的值类型[" + field.getValueType() + "]不合法");
            return;
        }
        if (field.getType().equals("map")) {
            //校验map键类型
            if (field.getKeyType() == null) {
                addValidatedError(getName4Validate("的") + field.getType() + "类型" + field.getName4Validate() + "的键类型不能为空");
                return;
            }
            if (!field.isPrimitiveKeyType()) {
                addValidatedError(getName4Validate("的") + field.getType() + "类型" + field.getName4Validate() + "的键类型[" + field.getKeyType() + "]不合法");
            }
        }
    }

    @Override
    public void validate2() {
        super.validate2();

        for (FieldDefinition field : fields) {
            //校验字段引用
            validateFieldRef(field);
        }
    }

    protected boolean supportFieldRef() {
        return true;
    }

    protected void validateFieldRef(FieldDefinition field) {
        if (!supportFieldRef()) {
            return;
        }
        if (field.getRef() == null) {
            return;
        }

        if (!field.getType().equals("map")) {
            String[] fieldRefs = field.getRef().split("[.]");
            if (fieldRefs.length != 2) {
                addValidatedError(getName4Validate() + field.getName4Validate() + "的引用格式错误[" + field.getRef() + "]，正确格式:[配置.字段]");
                return;
            }
            validateFieldRef(field, false, fieldRefs[0], fieldRefs[1]);
            return;
        }

        //map类型字段引用校验
        String[] fieldRefs = field.getRef().split("[,]");
        String mapRefPatternError = getName4Validate() + "的map类型字段" + field.getName4Validate() + "的引用格式错误[" + field.getRef() + "]，正确格式:[键引用的配置.字段]或者[键引用配置.字段,值引用的配置.字段]";
        if (fieldRefs.length != 1 && fieldRefs.length != 2) {
            addValidatedError(mapRefPatternError);
            return;
        }

        String[] fieldKeyRefs = fieldRefs[0].split("[.]");
        String[] fieldValueRefs = null;
        if (fieldRefs.length == 2) {
            fieldValueRefs = fieldRefs[1].split("[.]");
        }

        if (fieldKeyRefs.length != 2) {
            addValidatedError(mapRefPatternError);
            return;
        }
        validateFieldRef(field, true, fieldKeyRefs[0], fieldKeyRefs[1]);

        if (fieldValueRefs != null) {
            if (fieldValueRefs.length != 2) {
                addValidatedError(mapRefPatternError);
                return;
            }
            validateFieldRef(field, false, fieldValueRefs[0], fieldValueRefs[1]);
        }
    }

    protected void validateFieldRef(FieldDefinition field, boolean keType, String refConfigName, String refFiledName) {
        String refConfigAndField = refConfigName + "." + refFiledName;

        ClassDefinition refClass = ClassDefinition.getAll().get(refConfigName);
        if (!(refClass instanceof ConfigDefinition)) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用配置[" + refConfigName + "]不存在");
            return;
        }

        ConfigDefinition refConfig = (ConfigDefinition) refClass;
        FieldDefinition refField = refConfig.getField(refFiledName);

        if (refField == null) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用字段[" + refConfigAndField + "]不存在");
            return;
        }

        if (refField == field) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "不能引用自己");
            return;
        }
        if (field.isCollectionType()) {
            if (keType && !field.getKeyType().equals(refField.getType())) {
                addValidatedError(getName4Validate("的") + "map类型" + field.getName4Validate() + "的键类型[" + field.getKeyType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
            }
            if (!keType && !field.getValueType().equals(refField.getType())) {
                addValidatedError(getName4Validate() + field.getType() + "类型字段" + field.getName4Validate() + "的值类型[" + field.getValueType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
            }
        } else if (!field.getType().equals(refField.getType())) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "的类型[" + field.getType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
        }

        IndexDefinition refFieldIndex = refConfig.getIndexByStartField(refField);
        if (refFieldIndex == null) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用字段[" + refConfigAndField + "]不是一级索引");
        }

    }

    public static boolean isBeanDefinition(String type) {
        return isBeanDefinition(ClassDefinition.getAll().get(type));
    }

    public static boolean isBeanDefinition(ClassDefinition classDefinition) {
        if (!(classDefinition instanceof BeanDefinition)) {
            return false;
        }
        return !(classDefinition instanceof DataDefinition) && !(classDefinition instanceof MessageDefinition) && !(classDefinition instanceof ConfigDefinition);
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
