package quan.generator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class FieldDefinition extends Definition {

    private String type;

    private String value;

    private boolean optional;

    private String source;

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

    private boolean enumType;//是否是枚举

    private ClassDefinition classDefinition;


    public static final List<String> BUILT_IN_TYPES = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string", "bytes", "list", "set", "map");

    public static final List<String> PRIMITIVE_TYPES = Arrays.asList("bool", "byte", "short", "int", "long", "float", "double", "string");


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
        return enumType;
    }

    public void setEnumType(boolean enumType) {
        this.enumType = enumType;
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

    public String getsValueTypeWithPackage() {
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


    public String getSource() {
        return source;
    }

    public FieldDefinition setSource(String source) {
        this.source = source;
        return this;
    }


    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public FieldDefinition setClassDefinition(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
        return this;
    }
}
