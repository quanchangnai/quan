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

    @Override
    public BeanDefinition setCategory(DefinitionCategory category) {
        this.category = category;
        return this;
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
        validateFieldType(field);

        //校验字段循环依赖
        validateFieldBeanCycle(field);

    }

    protected void validateFieldType(FieldDefinition field) {
        if (field.getTypes() == null) {
            addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型不能为空" );
            return;
        }

        String[] fieldTypes = field.getTypes().split(":" );
        String fieldType = fieldTypes[0];

        if (fieldTypes.length == 1 && StringUtils.isBlank(fieldType)) {
            addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型不能为空" );
            return;
        }

        field.setType(fieldType);
        if (!field.isLegalType()) {
            addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + fieldType + "]不合法" );
            field.setType(null);
            return;
        }


        if (!field.isCollectionType() && fieldTypes.length != 1) {
            addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getTypes() + "]格式错误" );
            return;
        }

        if (fieldType.equals("list" ) || fieldType.equals("set" )) {
            if (fieldTypes.length == 2 && !StringUtils.isBlank(fieldTypes[1])) {
                field.setValueType(fieldTypes[1]);
                if (!field.isLegalValueType()) {
                    addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getType() + "]的值类型[" + field.getValueType() + "]不合法" );
                }
            } else {
                addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getTypes() + "]格式错误，合法格式[" + fieldType + ":值类型]" );
            }
        }

        if (fieldType.equals("map" )) {
            if (fieldTypes.length == 3 && !StringUtils.isBlank(fieldTypes[1]) && !StringUtils.isBlank(fieldTypes[2])) {
                field.setKeyType(fieldTypes[1]);
                field.setValueType(fieldTypes[2]);
                if (!field.isPrimitiveKeyType()) {
                    addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getType() + "]的键类型[" + field.getKeyType() + "]不合法" );
                }
                if (!field.isLegalValueType()) {
                    addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getType() + "]的值类型[" + field.getValueType() + "]不合法" );
                }
            } else {
                addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getTypes() + "]格式错误，合法格式[" + fieldType + ":键类型:值类型]" );
            }
        }

    }

    /**
     * 校验字段循环依赖，字段类型为bean类型或者集合类型字段的值类型为bean
     */
    protected void validateFieldBeanCycle(FieldDefinition field) {
        Set<BeanDefinition> fieldBeans = new HashSet<>();
        fieldBeans.add(this);
        boolean cycle = validateFieldBeanCycle(field, field, fieldBeans);
        field.setCycle(cycle);
    }

    protected boolean validateFieldBeanCycle(FieldDefinition rootField, FieldDefinition field, Set<BeanDefinition> fieldBeans) {
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
            addValidatedError(getName4Validate("的" ) + rootField.getName4Validate() + "循环依赖类型[" + fieldBean.getName() + "]" );
            return true;
        }

        fieldBeans.add(fieldBean);

        for (FieldDefinition fieldBeanField : fieldBean.getFields()) {
            if (validateFieldBeanCycle(rootField, fieldBeanField, fieldBeans)) {
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
        if (category != DefinitionCategory.config || getClass() != BeanDefinition.class) {
            return;
        }
        if (delimiter.length() != 1) {
            addValidatedError(getName4Validate() + "的分隔符[" + delimiter + "]长度必须1个字符" );
        }
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (!Constants.LEGAL_DELIMITERS.contains(s)) {
                addValidatedError(getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
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

    protected void validateFieldRef(FieldDefinition field) {
        if (field.getType() == null || getCategory() != DefinitionCategory.config || field.getRef() == null) {
            return;
        }

        if (!field.getType().equals("map" )) {
            String[] fieldRefs = field.getRef().split("\\." );
            if (fieldRefs.length != 2) {
                addValidatedError(getName4Validate() + field.getName4Validate() + "的引用格式错误[" + field.getRef() + "]，正确格式:[配置.字段]" );
                return;
            }
            validateFieldRef(field, false, fieldRefs[0], fieldRefs[1]);
            return;
        }

        //map类型字段引用校验
        String[] fieldRefs = field.getRef().split(":" );
        String refPatternError = getName4Validate("的" ) + field.getName4Validate() + "类型[map]的引用格式错误[" + field.getRef() + "]，正确格式:[键引用的配置.字段]或者[键引用配置.字段:值引用的配置.字段]";
        if (fieldRefs.length != 1 && fieldRefs.length != 2) {
            addValidatedError(refPatternError);
            return;
        }

        String[] fieldKeyRefs = fieldRefs[0].split("\\." );
        String[] fieldValueRefs = null;
        if (fieldRefs.length == 2) {
            fieldValueRefs = fieldRefs[1].split("\\." );
        }

        if (fieldKeyRefs.length != 2) {
            addValidatedError(refPatternError);
            return;
        }
        validateFieldRef(field, true, fieldKeyRefs[0], fieldKeyRefs[1]);

        if (fieldValueRefs != null) {
            if (fieldValueRefs.length != 2) {
                addValidatedError(refPatternError);
                return;
            }
            validateFieldRef(field, false, fieldValueRefs[0], fieldValueRefs[1]);
        }
    }

    protected void validateFieldRef(FieldDefinition field, boolean keType, String refConfigName, String refFiledName) {
        String refConfigAndField = refConfigName + "." + refFiledName;

        ConfigDefinition refConfig = parser.getConfig(refConfigName);
        if (refConfig == null) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用配置[" + refConfigName + "]不存在" );
            return;
        }

        FieldDefinition refField = refConfig.getField(refFiledName);

        if (refField == null) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用字段[" + refConfigAndField + "]不存在" );
            return;
        }

        if (refField == field) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "不能引用自己" );
            return;
        }
        if (field.isCollectionType()) {
            if (keType && field.isPrimitiveKeyType() && !field.getKeyType().equals(refField.getType())) {
                addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getType() + "]的键类型[" + field.getKeyType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致" );
            }
            if (!keType && field.isLegalValueType() && !field.getValueType().equals(refField.getType())) {
                addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "类型[" + field.getType() + "]的值类型[" + field.getValueType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致" );
            }
        } else if (!field.getType().equals(refField.getType())) {
            addValidatedError(getName4Validate("的" ) + field.getName4Validate() + "的类型[" + field.getType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致" );
        }

        IndexDefinition refFieldIndex = refConfig.getIndexByField1(refField);
        if (refFieldIndex == null) {
            addValidatedError(getName4Validate() + field.getName4Validate() + "的引用字段[" + refConfigAndField + "]不是一级索引" );
        }

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