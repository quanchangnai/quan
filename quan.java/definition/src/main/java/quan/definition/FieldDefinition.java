package quan.definition;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.IndexDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 字段定义，被数据库、消息和配置共用
 * Created by quanchangnai on 2017/7/6.
 */
public class FieldDefinition extends Definition {

    //字段类型的原始定义,集合类型包含其元素类型
    private String originType;

    //拆分后的字段类型
    private String type;
    private String keyType;
    private String valueType;

    //内建类型对应的特定语言基本类型，自定义类型保持不变
    private String basicType;
    private String basicKeyType;
    private String basicValueType;

    //内建类型对应的特定语言具体类型，自定义类型保持不变
    private String classType;
    private String classKeyType;
    private String classValueType;

    //字段类型是否有循环依赖
    private boolean cycle;

    //枚举字段值
    private String value;

    //消息字段,是否可选
    private boolean optional;

    //消息字段,小数保留的精度
    private int scale = -1;

    //数据库字段,忽略存储
    private boolean ignore;

    //配置字段,对应表格中的列
    private String column;

    //配置字段,索引类型
    private String index;

    //配置字段,集合类型字段的分隔符
    private String delimiter;

    //配置字段,转义后的集合类型字段的分隔符
    private String escapedDelimiter;

    //配置字段,引用[配置.字段]
    private String ref;

    //配置字段,是支持还是排除设置的语言
    private boolean excludeLanguage;

    //配置字段,支持或者排除的语言
    protected Set<String> languages = new HashSet<>();

    //配置字段,字段对应的表格列号,对象或者集合类型可能会对应多列，校验表头时设置
    private List<Integer> columnNums = new ArrayList<>();

    public FieldDefinition() {
    }

    @Override
    public int getDefinitionType() {
        return 4;
    }

    @Override
    public String getDefinitionTypeName() {
        return "字段";
    }

    @Override
    protected Pattern namePattern() {
        return Constants.FIELD_NAME_PATTERN;
    }


    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        if (StringUtils.isBlank(originType)) {
            return;
        }
        this.originType = originType;
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
        ClassDefinition classDefinition = parser.getClass(getType());
        if (classDefinition instanceof EnumDefinition) {
            return (EnumDefinition) classDefinition;
        }
        return null;
    }

    public boolean isBeanType() {
        return getBean() != null;
    }

    public ClassDefinition getClassDefinition() {
        return parser.getClass(getType());
    }

    public BeanDefinition getBean() {
        ClassDefinition classDefinition = parser.getClass(getType());
        if (BeanDefinition.isBeanDefinition(classDefinition)) {
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


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        this.value = value.trim();
    }

    public boolean isOptional() {
        return optional;
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
        return getValueBean() != null;
    }

    public BeanDefinition getValueBean() {
        if (!isCollectionType()) {
            return null;
        }
        ClassDefinition classDefinition = parser.getClass(getValueType());
        if (BeanDefinition.isBeanDefinition(classDefinition)) {
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
        return column;
    }

    public FieldDefinition setColumn(String column) {
        if (StringUtils.isBlank(column)) {
            return this;
        }
        this.column = column.trim();
        return this;
    }


    public String getIndex() {
        return index;
    }

    public FieldDefinition setIndex(String index) {
        if (IndexDefinition.isUnique(index) || IndexDefinition.isNormal(index) || index != null && index.trim().equals("-")) {
            this.index = index.trim();
        }
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
        if (StringUtils.isEmpty(escapedDelimiter)) {
            escapedDelimiter = ConfigDefinition.escapeDelimiter(getDelimiter());
        }
        return escapedDelimiter;
    }

    public FieldDefinition setDelimiter(String delimiter) {
        if (StringUtils.isBlank(delimiter)) {
            return this;
        }
        this.delimiter = delimiter.trim();
        return this;
    }

    public String getRef() {
        return ref;
    }

    public FieldDefinition setRef(String ref) {
        if (StringUtils.isBlank(ref)) {
            return this;
        }
        this.ref = ref.trim();
        return this;
    }

    /**
     * 返回字段的引用配置类
     *
     * @param keyRef true:map类型字段的键引用,false:map list set类型字段的值引用或者原生类型字段的引用
     */
    public ConfigDefinition getRefConfig(boolean keyRef) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }

        if (type.equals("map")) {
            String[] fieldRefs = ref.split(":", -1);
            ConfigDefinition refConfig = null;

            if (keyRef && fieldRefs.length >= 1) {
                refConfig = parser.getConfig(fieldRefs[0].split("\\.", -1)[0]);
            }
            if (!keyRef && fieldRefs.length == 2) {
                refConfig = parser.getConfig(fieldRefs[1].split("\\.", -1)[0]);
            }
            return refConfig;
        }

        //list set 原生类型
        String[] fieldRefs = ref.split("\\.", -1);
        if (fieldRefs.length != 2) {
            return null;
        }
        return parser.getConfig(fieldRefs[0]);
    }

    /**
     * 返回字段的引用字段
     *
     * @param keyRef true:map类型字段的键引用,false:map list set类型字段的值引用或者原生类型字段的引用
     * @return 字段的引用字段
     */
    public FieldDefinition getRefField(boolean keyRef) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }
        if (type.equals("map")) {
            String[] fieldRefs = ref.split(",", -1);
            ConfigDefinition refConfig;

            if (keyRef && fieldRefs.length >= 1) {
                String[] fieldKeyRefs = fieldRefs[0].split("\\.", -1);
                refConfig = parser.getConfig(fieldKeyRefs[0]);
                if (refConfig != null) {
                    return refConfig.getField(fieldKeyRefs[1]);
                }
            }
            if (!keyRef && fieldRefs.length == 2) {
                String[] fieldValueRefs = fieldRefs[1].split("\\.", -1);
                refConfig = parser.getConfig(fieldValueRefs[0]);
                if (refConfig != null) {
                    return refConfig.getField(fieldValueRefs[1]);
                }
            }
            return null;
        }

        //list set 原生类型
        String[] fieldRefs = ref.split("\\.", -1);
        if (fieldRefs.length != 2) {
            return null;
        }

        ConfigDefinition refConfig = parser.getConfig(fieldRefs[0]);
        if (refConfig != null) {
            return refConfig.getField(fieldRefs[1]);
        }
        return null;
    }

    public void setLanguage(String language) {
        if (StringUtils.isBlank(language) || category != Category.config) {
            return;
        }
        Pair<Boolean, Set<String>> pair = Language.parse(language);
        excludeLanguage = pair.getLeft();
        languages = pair.getRight();
    }

    public boolean isExcludeLanguage() {
        return excludeLanguage;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * 字段本身是否支持特定语言，实际使用要先判断字段所在的配置是否支持该语言
     */
    public boolean supportLanguage(String language) {
        boolean support = languages.isEmpty() || languages.contains(language);
        if (excludeLanguage) {
            support = !support;
        }
        return support;
    }

    public boolean supportLanguage(Language language) {
        return supportLanguage(language.name());
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
        BeanDefinition beanDefinition = getBean();
        if (beanDefinition != null) {
            if (beanDefinition.hasChild()) {
                return columnNums.size() == beanDefinition.getDescendantMaxFieldCount() + 1;
            } else {
                return columnNums.size() == beanDefinition.getFields().size();
            }
        } else if (type.equals("map")) {
            return columnNums.size() > 0 && columnNums.size() % 2 == 0;
        }
        return true;
    }
}
