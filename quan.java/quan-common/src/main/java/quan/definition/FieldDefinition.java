package quan.definition;

import quan.definition.config.ConfigDefinition;
import quan.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 字段定义，被数据、消息和配置共用
 */
public class FieldDefinition extends Definition implements Cloneable {

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-z][a-zA-Z\\d]*");

    private ClassDefinition owner;

    //原始定义的字段类型,集合类型包含其元素类型
    private String typeInfo;

    //拆分后的字段类型
    private String type;
    private String keyType;
    private String valueType;

    //内建类型对应的特定语言基本类型，自定义类型保持不变
    private String basicType;
    private String keyBasicType;
    private String valueBasicType;

    //内建类型对应的特定语言具体类型，自定义类型保持不变
    private String classType;
    private String keyClassType;
    private String valueClassType;

    //字段类型依赖是否有循环
    private boolean cycle;

    //数字类型限制：最小值
    private Object min;

    //数字类型限制：最小值
    private Object max;

    //枚举值
    private String enumValue;

    //消息:字段ID
    private String id;

    //消息:是否可选
    private boolean optional;

    //消息:小数保留的精度
    private int scale = -1;

    //消息、数据:忽略编码
    private boolean ignore;

    ///配置、数据:索引类型
    private String index;

    //配置:对应表格中的列名
    private String column;

    //配置:集合类型字段的分隔符
    private String delimiter;

    //配置:转义后的字段分隔符
    private List<String> escapedDelimiters = new ArrayList<>();

    //配置:引用[配置.字段]
    private String ref;
    private String refType;//语言相关

    private String languageStr;

    //配置:支持的语言
    protected Set<String> languages = new HashSet<>();

    //配置:字段对应的表格列号,对象或者集合类型可能会对应多列，校验表头时设置
    private List<Integer> columnNums = new ArrayList<>();

    //配置:校验表达式
    private Object validation;

    public FieldDefinition() {
    }

    @Override
    public int getKind() {
        return KIND_FIELD;
    }

    @Override
    public String getKindName() {
        return "字段";
    }

    public ClassDefinition getOwner() {
        return owner;
    }

    public FieldDefinition setOwner(ClassDefinition owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (name != null) {
            underscoreName = StringUtils.toSnakeCase(name, false);
        }
    }

    @Override
    public Pattern getNamePattern() {
        return NAME_PATTERN;
    }


    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        if (!StringUtils.isBlank(typeInfo)) {
            this.typeInfo = typeInfo;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (!StringUtils.isBlank(type)) {
            this.type = type.trim();
        } else {
            this.type = null;
        }
    }

    public boolean isBuiltinType() {
        return isBuiltinType(type);
    }

    public boolean isBuiltinType(String type) {
        if (category == Category.data) {
            return Constants.DATA_BUILTIN_TYPES.contains(type);
        }
        if (category == Category.message) {
            return Constants.MESSAGE_BUILTIN_TYPES.contains(type);
        }
        if (category == Category.config) {
            return Constants.CONFIG_BUILTIN_TYPES.contains(type);
        }

        return false;
    }

    public boolean isIntegralNumberType() {
        return Constants.INTEGRAL_NUMBER_TYPES.contains(type);
    }

    public boolean isNumberType() {
        return Constants.NUMBER_TYPES.contains(type);
    }

    public boolean isCollectionType() {
        return Constants.COLLECTION_TYPES.contains(type);
    }

    public boolean isPrimitiveType() {
        return Constants.PRIMITIVE_TYPES.contains(type);
    }

    public boolean isEnumType() {
        return getEnum() != null;
    }

    public EnumDefinition getEnum() {
        ClassDefinition classDefinition = getClassDefinition();
        if (classDefinition instanceof EnumDefinition) {
            return (EnumDefinition) classDefinition;
        }
        return null;
    }

    public boolean isBeanType() {
        return getTypeBean() != null;
    }

    public ClassDefinition getClassDefinition() {
        return parser.getClass(owner, type);
    }

    public BeanDefinition getTypeBean() {
        ClassDefinition classDefinition = getClassDefinition();
        if (classDefinition != null && classDefinition.getClass() == BeanDefinition.class) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }

    public boolean isTimeType() {
        return Constants.TIME_TYPES.contains(type);
    }

    /**
     * 类型是否合法
     */
    public boolean isLegalType() {
        return isBuiltinType() || isBeanType() || isEnumType() || (category == Category.config && isTimeType());
    }

    public boolean isMapType() {
        return "map".equals(type);
    }

    public boolean isListType() {
        return "list".equals(type);
    }

    public boolean isSetType() {
        return "set".equals(type);
    }

    public boolean isStringType() {
        return "string".equals(type);
    }

    public boolean isBytesType() {
        return "bytes".equals(type);
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        if (min instanceof String) {
            min = ((String) min).trim();
        }
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        if (max instanceof String) {
            max = ((String) max).trim();
        }
        this.max = max;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        if (StringUtils.isBlank(enumValue)) {
            return;
        }
        this.enumValue = enumValue.trim();
    }

    public boolean isOptional() {
        return optional;
    }

    public FieldDefinition setId(String id) {
        if (!StringUtils.isBlank(id)) {
            this.id = id;
        }
        return this;
    }

    public String getId() {
        return id;
    }

    //消息tag
    public int getTag() {
        int t;
        if (type.equals("bool") || isIntegralNumberType() || isEnumType() || scale >= 0) {
            t = 0;
        } else if (type.equals("float")) {
            t = 1;
        } else if (type.equals("double")) {
            t = 2;
        } else {
            //bytes、string、set、list、map、bean
            t = 3;
        }
        return Integer.parseInt(id) << 2 | t;
    }

    public FieldDefinition setOptional(String optional) {
        if (!StringUtils.isBlank(optional) && optional.trim().equals("true")) {
            this.optional = true;
        }
        return this;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        if (StringUtils.isBlank(keyType)) {
            return;
        }
        this.keyType = keyType.trim();
    }

    public boolean isBuiltinKeyType() {
        return isBuiltinType(keyType);
    }

    public boolean isPrimitiveKeyType() {
        return Constants.PRIMITIVE_TYPES.contains(keyType);
    }

    public boolean isStringKeyType() {
        return keyType.equals("string");
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        if (StringUtils.isBlank(valueType)) {
            return;
        }
        this.valueType = valueType.trim();
    }

    public boolean isBuiltinValueType() {
        return isBuiltinType(valueType);
    }

    public boolean isPrimitiveValueType() {
        return Constants.PRIMITIVE_TYPES.contains(valueType);
    }

    public boolean isNumberValueType() {
        return Constants.NUMBER_TYPES.contains(valueType);
    }

    public boolean isBeanValueType() {
        return getValueTypeBean() != null;
    }

    public boolean isStringValueType() {
        return valueType.equals("string");
    }

    public BeanDefinition getValueTypeBean() {
        if (!isCollectionType()) {
            return null;
        }
        ClassDefinition classDefinition = parser.getClass(owner, getValueType());
        if (classDefinition != null && classDefinition.getClass() == BeanDefinition.class) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }

    /**
     * 集合值类型是否合法
     */
    public boolean isLegalValueType() {
        return isPrimitiveValueType() || isBeanValueType();
    }

    public String getBasicType() {
        if (isEnumType() || isBeanType()) {
            return getClassType();
        }
        if (basicType == null) {
            return getType();
        }
        return basicType;
    }

    public void setBasicType(String basicType) {
        this.basicType = basicType;
    }

    public String getKeyBasicType() {
        if (keyBasicType == null) {
            return getKeyType();
        }
        return keyBasicType;
    }

    public void setKeyBasicType(String keyBasicType) {
        this.keyBasicType = keyBasicType;
    }

    public String getValueBasicType() {
        if (valueBasicType == null) {
            return getValueType();
        }
        return valueBasicType;
    }

    public void setValueBasicType(String valueBasicType) {
        this.valueBasicType = valueBasicType;
    }

    public String getClassType() {
        if (classType == null) {
            return ClassDefinition.getShortName(type);
        }
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getKeyClassType() {
        if (keyClassType == null) {
            return getKeyType();
        }
        return keyClassType;
    }

    public void setKeyClassType(String keyClassType) {
        this.keyClassType = keyClassType;
    }

    public String getValueClassType() {
        if (valueClassType == null) {
            return ClassDefinition.getShortName(getValueType());
        }
        return valueClassType;
    }

    public void setValueClassType(String valueClassType) {
        this.valueClassType = valueClassType;
    }

    public boolean isCycle() {
        return cycle;
    }

    public FieldDefinition setCycle(boolean cycle) {
        this.cycle = cycle;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public FieldDefinition setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public FieldDefinition setIgnore(String ignore) {
        if (!StringUtils.isBlank(ignore) && ignore.trim().equals("true")) {
            this.ignore = true;
        }
        return this;
    }

    public String getColumn() {
        if (StringUtils.isBlank(column)) {
            return getName();
        }
        return column;
    }

    public FieldDefinition setColumn(String column) {
        if (!StringUtils.isBlank(column)) {
            this.column = column.trim();
        }
        return this;
    }


    public String getIndex() {
        return index;
    }

    public FieldDefinition setIndex(String index) {
        if (!StringUtils.isBlank(index)) {
            this.index = index.trim();
        }
        return this;
    }

    public String getDelimiter() {
        if (delimiter != null) {
            return delimiter;
        }
        if (isListType() || isSetType()) {
            return ";";
        } else if (isMapType()) {
            return ";*";
        }
        return null;
    }

    public FieldDefinition setDelimiter(String delimiter) {
        if (!StringUtils.isBlank(delimiter)) {
            this.delimiter = delimiter.trim();
        }
        return this;
    }

    public List<String> getEscapedDelimiters() {
        if (!escapedDelimiters.isEmpty()) {
            return escapedDelimiters;
        }

        String _delimiter = getDelimiter();
        for (int i = 0; i < _delimiter.length(); i++) {
            escapedDelimiters.add(ConfigDefinition.escapeDelimiter(String.valueOf(_delimiter.charAt(i))));
        }

        return escapedDelimiters;
    }

    public String getRef() {
        return ref;
    }

    public FieldDefinition setRef(String ref) {
        if (!StringUtils.isBlank(ref)) {
            this.ref = ref.trim();
        }
        return this;
    }

    public String getRefType() {
        if (refType == null) {
            ConfigDefinition refConfig = getRefConfig();
            if (refConfig != null) {
                return refConfig.getName();
            }
        }
        return refType;
    }

    public FieldDefinition setRefType(String refType) {
        this.refType = refType;
        return this;
    }

    public boolean isSimpleRef() {
        if (isCollectionType()) {
            return false;
        }
        IndexDefinition refIndex = getRefIndex(false);
        return refIndex != null && refIndex.getFields().size() == 1;
    }

    public IndexDefinition getRefIndex() {
        return getRefIndex(false);
    }

    public IndexDefinition getRefIndex(boolean keyRef) {
        ConfigDefinition refConfig = getRefConfig(keyRef);
        FieldDefinition refField = getRefField(keyRef);
        if (refConfig == null || refField == null) {
            return null;
        }
        return refConfig.getIndexByField1(refField);
    }

    public ConfigDefinition getRefConfig() {
        return getRefConfig(false);
    }

    /**
     * 返回字段的引用配置类
     *
     * @param isKeyRef true:map类型字段的键引用,false:map list set类型字段的值引用或者原生类型字段的引用
     */
    public ConfigDefinition getRefConfig(boolean isKeyRef) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }

        if (isMapType()) {
            String[] fieldRefs = ref.split("[:：]", -1);
            ConfigDefinition refConfig = null;

            if (isKeyRef && fieldRefs.length >= 1) {
                String keyRef = owner.resolveFieldRef(fieldRefs[0]);
                String refConfigName = keyRef.substring(0, keyRef.lastIndexOf("."));
                refConfig = parser.getConfig(owner, refConfigName);
            }
            if (!isKeyRef && fieldRefs.length == 2) {
                String valueRef = owner.resolveFieldRef(fieldRefs[1]);
                String refConfigName = valueRef.substring(0, valueRef.lastIndexOf("."));
                refConfig = parser.getConfig(owner, refConfigName);
            }
            return refConfig;
        }

        //list set 原生类型
        String fieldRef = owner.resolveFieldRef(ref);
        String refConfigName = fieldRef.substring(0, fieldRef.lastIndexOf("."));
        return parser.getConfig(owner, refConfigName);
    }

    public FieldDefinition getRefField() {
        return getRefField(false);
    }

    /**
     * 返回字段的引用字段
     *
     * @param isKeyRef true:map类型字段的键引用,false:map list set类型字段的值引用或者原生类型字段的引用
     * @return 字段的引用字段
     */
    public FieldDefinition getRefField(boolean isKeyRef) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }

        if (isMapType()) {
            String[] fieldRefs = ref.split("[,，]", -1);
            ConfigDefinition refConfig;

            if (isKeyRef && fieldRefs.length >= 1) {
                String keyRef = owner.resolveFieldRef(fieldRefs[0]);
                String refConfigName = keyRef.substring(0, keyRef.lastIndexOf("."));
                refConfig = parser.getConfig(owner, refConfigName);
                if (refConfig != null) {
                    String refFieldName = keyRef.substring(keyRef.lastIndexOf(".") + 1);
                    return refConfig.getField(refFieldName);
                }
            }
            if (!isKeyRef && fieldRefs.length == 2) {
                String valueRef = owner.resolveFieldRef(fieldRefs[1]);
                String refConfigName = valueRef.substring(0, valueRef.lastIndexOf("."));
                refConfig = parser.getConfig(owner, refConfigName);
                if (refConfig != null) {
                    String refFieldName = valueRef.substring(valueRef.lastIndexOf(".") + 1);
                    return refConfig.getField(refFieldName);
                }
            }
            return null;
        }

        //list set 原生类型
        String fieldRef = owner.resolveFieldRef(ref);
        String refConfigName = fieldRef.substring(0, fieldRef.lastIndexOf("."));
        ConfigDefinition refConfig = parser.getConfig(owner, refConfigName);

        if (refConfig != null) {
            String refFieldName = fieldRef.substring(fieldRef.lastIndexOf(".") + 1);
            return refConfig.getField(refFieldName);
        }
        return null;
    }

    public void setLanguageStr(String languageStr) {
        this.languageStr = languageStr;
    }

    public String getLanguageStr() {
        return languageStr;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * 字段本身是否支持特定语言，实际使用要先判断字段所在的配置是否支持该语言
     */
    public boolean isSupportedLanguage(String language) {
        return languages.contains(language);
    }

    public boolean isSupportedLanguage(Language language) {
        return isSupportedLanguage(language.name());
    }

    public List<Integer> getColumnNums() {
        return columnNums;
    }

    public Integer getLastColumnNum() {
        return columnNums.get(columnNums.size() - 1);
    }

    public boolean isLegalColumnCount() {
        if (columnNums.size() == 1) {
            return true;
        }

        BeanDefinition beanDefinition = getTypeBean();
        if (beanDefinition != null) {
            if (beanDefinition.hasChildren()) {
                return columnNums.size() == beanDefinition.getDescendantMaxFieldCount() + 1;
            } else {
                return columnNums.size() == beanDefinition.getFields().size();
            }
        } else if (isMapType()) {
            return !columnNums.isEmpty() && columnNums.size() % 2 == 0;
        }

        return true;
    }

    public Object getValidation() {
        return validation;
    }

    public void setValidation(Object validation) {
        this.validation = validation;
    }

    @Override
    public FieldDefinition clone() {
        try {
            return (FieldDefinition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getDefaultValue() {
        if (Constants.NUMBER_TYPES.contains(type)) {
            return 0;
        } else if ("string".equals(type)) {
            return "";
        } else if ("bool".equals(type)) {
            return false;
        } else {
            return null;
        }
    }

}
