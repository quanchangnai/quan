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

    //允许的分隔符
    public static final Set<String> allowDelimiters = new HashSet<>(Arrays.asList(";", "_", "*", "|", "$", "@", "#", "&", "?"));

    //需要转义的分隔符(正则表达式特殊字符)
    public static final Set<String> needEscapeChars = new HashSet<>(Arrays.asList("*", "|", "?"));

    //内建类型
    public static final List<String> builtInTypes = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string", "bytes", "list", "set", "map");

    //原生类型
    public static final List<String> primitiveTypes = Arrays.asList("bool", "short", "int", "long", "float", "double", "string");

    //事件类型
    public static final List<String> timeTypes = Arrays.asList("date", "time", "datetime");

    public FieldDefinition() {
    }

    @Override
    public int getDefinitionType() {
        return 4;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        if (type == null || type.trim().equals("")) {
            return;
        }
        this.type = type;
    }

    public boolean isBuiltInType() {
        return builtInTypes.contains(type);
    }

    public boolean isCollectionType() {
        return type.equals("list") || type.equals("set") || type.equals("map");
    }

    public boolean isPrimitiveType() {
        return primitiveTypes.contains(type);
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

    public boolean isBuiltInKeyType() {
        return builtInTypes.contains(keyType);
    }

    public boolean isPrimitiveKeyType() {
        return primitiveTypes.contains(keyType);
    }


    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        if (valueType == null || valueType.trim().equals("")) {
            return;
        }
        this.valueType = valueType;
    }

    public boolean isBuiltInValueType() {
        return builtInTypes.contains(valueType);
    }

    public boolean isPrimitiveValueType() {
        return primitiveTypes.contains(valueType);
    }

    public boolean isBeanValueType() {
        return getValueBean() != null;
    }

    public BeanDefinition getValueBean() {
        if (!isCollectionType()) {
            return null;
        }
        ClassDefinition classDefinition = ClassDefinition.getAll().get(getValueType());
        if (classDefinition instanceof BeanDefinition) {
            return (BeanDefinition) classDefinition;
        }
        return null;
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

    /**
     * 类型是否合法
     */
    public boolean isLegalType() {
        return isBuiltInType() || isBeanType() || isEnumType();
    }

    /**
     * 集合值类型是否合法
     */
    public boolean isLegalValueType() {
        return isPrimitiveValueType() || isBeanValueType();
    }
}
