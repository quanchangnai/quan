package quan.generator;

import quan.generator.config.ConfigDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class FieldDefinition extends Definition {

    private String type;

    private boolean optional;

    private String keyType;
    private String valueType;

    //内建类型对应的具体语言实现类型
    private String basicType;
    private String basicKeyType;
    private String basicValueType;

    //内建类型对应的具体语言Class类型
    private String classType;
    private String classKeyType;
    private String classValueType;

    //枚举值
    private String value;

    //对应配置表格中的列
    private String column;

    //配置的索引类型
    private String index;

    //配置集合类型字段的分隔符
    private String delimiter;

    public static Set<String> allowDelimiters = new HashSet<>(Arrays.asList(";", "_", "*", "|", "$", "@", "#", "&", "?"));

    public static Set<String> needEscapeChars = new HashSet<>(Arrays.asList("*", "|", "?"));

    public static final List<String> BUILT_IN_TYPES = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string", "bytes", "list", "set", "map");

    public static final List<String> PRIMITIVE_TYPES = Arrays.asList("bool", "short", "int", "long", "float", "double", "string");

    public FieldDefinition() {
    }

    @Override
    public int getDefinitionType() {
        return 4;
    }

    public String getType() {
        if (isTypeWithPackage()) {
            return type.substring(type.lastIndexOf(".") + 1);
        }
        return type;
    }

    public boolean isTypeWithPackage() {
        return type.contains(".");
    }

    public String getTypeWithPackage() {
        if (!isTypeWithPackage()) {
            return null;
        }
        return type;
    }

    public String getTypePackage() {
        if (isTypeWithPackage()) {
            return type.substring(0, type.lastIndexOf("."));
        }
        return null;
    }

    public void setType(String type) {
        if (type == null || type.trim().equals("")) {
            return;
        }
        this.type = type;
    }

    public boolean isBuiltInType() {
        return BUILT_IN_TYPES.contains(type);
    }

    public boolean isCollectionType() {
        return type.equals("list") || type.equals("set") || type.equals("map");
    }

    public boolean isPrimitiveType() {
        return PRIMITIVE_TYPES.contains(type);
    }

    public boolean isEnumType() {
        return ClassDefinition.getAll().get(getType()) instanceof EnumDefinition;
    }

    public boolean isBeanType() {
        return getBean() != null;
    }

    public BeanDefinition getBean() {
        ClassDefinition classDefinition = ClassDefinition.getAll().get(getType());
        if (classDefinition instanceof BeanDefinition) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }

    public boolean isValueBeanType() {
        if (!isCollectionType()) {
            return false;
        }
        return ClassDefinition.getAll().get(getValueType()) instanceof BeanDefinition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isOptional() {
        return optional;
    }

    public FieldDefinition setOptional(String optional) {
        if (optional != null && optional.equals("true")) {
            this.optional = true;
        }
        return this;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        if (keyType == null || keyType.trim().equals("")) {
            return;
        }
        this.keyType = keyType;
    }

    public boolean isKeyBuiltInType() {
        return BUILT_IN_TYPES.contains(keyType);
    }

    public boolean isKeyPrimitiveType() {
        return PRIMITIVE_TYPES.contains(keyType);
    }

    public boolean isValueTypeWithPackage() {
        if (valueType != null) {
            return valueType.contains(".");
        }
        return false;
    }

    public String getValueTypeWithPackage() {
        if (!isValueTypeWithPackage()) {
            return null;
        }
        return valueType;
    }

    public String getValueTypePackage() {
        if (isValueTypeWithPackage()) {
            return valueType.substring(0, valueType.lastIndexOf("."));
        }
        return null;
    }

    public String getValueType() {
        if (isTypeWithPackage()) {
            return valueType.substring(valueType.lastIndexOf(".") + 1);
        }
        return valueType;
    }

    public void setValueType(String valueType) {
        if (valueType == null || valueType.trim().equals("")) {
            return;
        }
        this.valueType = valueType;
    }

    public boolean isValueBuiltInType() {
        return BUILT_IN_TYPES.contains(valueType);
    }

    public boolean isValuePrimitiveType() {
        return PRIMITIVE_TYPES.contains(valueType);
    }

    public String getBasicType() {
        if (basicType == null) {
            return getType();
        }
        return basicType;
    }

    public void setBasicType(String basicType) {
        this.basicType = basicType;
    }

    public String getBasicKeyType() {
        if (basicKeyType == null) {
            return getKeyType();
        }
        return basicKeyType;
    }

    public void setBasicKeyType(String basicKeyType) {
        this.basicKeyType = basicKeyType;
    }

    public String getBasicValueType() {
        if (basicValueType == null) {
            return getValueType();
        }
        return basicValueType;
    }

    public void setBasicValueType(String basicValueType) {
        this.basicValueType = basicValueType;
    }

    public String getClassType() {
        if (classType == null) {
            return getType();
        }
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassKeyType() {
        if (classKeyType == null) {
            return getKeyType();
        }
        return classKeyType;
    }

    public void setClassKeyType(String classKeyType) {
        this.classKeyType = classKeyType;
    }

    public String getClassValueType() {
        if (classValueType == null) {
            return getValueType();
        }
        return classValueType;
    }

    public void setClassValueType(String classValueType) {
        this.classValueType = classValueType;
    }


    public String getColumn() {
        return column;
    }

    public FieldDefinition setColumn(String column) {
        if (column == null || column.trim().equals("")) {
            return this;
        }
        this.column = column;
        return this;
    }


    public String getIndex() {
        return index;
    }

    public FieldDefinition setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getDelimiter() {
        if (delimiter != null) {
            return delimiter;
        }
        if (type.equals("list") || type.equals("set")) {
            return ";";
        } else if (type.equals("map")) {
            return "*;";
        }
        return null;
    }

    public String getEscapedDelimiter() {
        return ConfigDefinition.escapeDelimiter(getDelimiter());
    }

    public FieldDefinition setDelimiter(String delimiter) {
        if (delimiter == null || delimiter.trim().equals("")) {
            return this;
        }
        this.delimiter = delimiter;
        return this;
    }
}
