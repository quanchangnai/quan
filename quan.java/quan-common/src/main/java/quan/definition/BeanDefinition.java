package quan.definition;

import org.apache.commons.lang3.StringUtils;
import quan.definition.DependentSource.DependentType;
import quan.definition.config.ConfigDefinition;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Bean定义，被数据、消息和配置共用
 */
public class BeanDefinition extends ClassDefinition {

    //配置的父类
    protected String parentName;

    //和具体语言相关的父类名，可能会包含包名
    protected String parentClassName;

    //配置的所有后代类长类名
    protected TreeSet<String> descendants = new TreeSet<>();

    private final TreeSet<String> meAndDescendants = new TreeSet<>();

    //配置的所有子类
    protected Set<BeanDefinition> children = new HashSet<>();

    //和语言无关的带包子类名:和语言相关的完整子类名
    protected Map<String, String> dependentChildren = new HashMap<>();

    protected int descendantMaxFieldCount;

    protected List<FieldDefinition> selfFields = new ArrayList<>();

    //配置Bean的字段分隔符
    private String delimiter;

    //消息的字段ID，用来标记和识别字段，不能识别的字段丢弃掉
    private final Set<Integer> fieldIds = new HashSet<>();

    public BeanDefinition() {
    }

    public BeanDefinition(String parent, String delimiter) {
        this.parentName = parent;
        this.delimiter = delimiter;
    }

    @Override
    public int getKind() {
        return KIND_BEAN;
    }

    @Override
    public BeanDefinition setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public String getKindName() {
        return super.getKindName();
    }

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getBeanNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
    }

    public List<FieldDefinition> getSelfFields() {
        return selfFields;
    }

    @Override
    public void addField(FieldDefinition fieldDefinition) {
        super.addField(fieldDefinition);
        selfFields.add(fieldDefinition);
    }

    public BeanDefinition setParentName(String parentName) {
        if (!StringUtils.isBlank(parentName)) {
            this.parentName = parentName.trim();
        }
        return this;
    }

    public String getParentName() {
        return parentName;
    }

    public BeanDefinition setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
        return this;
    }

    public String getParentClassName() {
        if (parentClassName == null) {
            return getShortName(parentName);
        }
        return parentClassName;
    }

    public BeanDefinition getParent() {
        return parser.getBean(this, parentName);
    }

    public Set<BeanDefinition> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public TreeSet<String> getMeAndDescendants() {
        if (meAndDescendants.isEmpty()) {
            meAndDescendants.addAll(descendants);
            meAndDescendants.add(getLongName());
        }
        return meAndDescendants;
    }


    public int getDescendantMaxFieldCount() {
        return descendantMaxFieldCount == 0 ? fields.size() : descendantMaxFieldCount;
    }

    public Map<String, String> getDependentChildren() {
        return dependentChildren;
    }

    @Override
    public void validate1() {
        super.validate1();

        if (!fieldIds.isEmpty() && fieldIds.size() != fields.size()) {
            addValidatedError(getValidatedName("的") + "所有字段必须要同时定义ID或者同时不定义ID");
        }
    }

    @Override
    public void validate2() {
        validateParent();

        for (FieldDefinition field : fields) {
            validateFieldLanguage(field);
            validateFieldNameDuplicate(field);
        }
    }

    @Override
    public void validate3() {
        super.validate3();
        validateDelimiter();

        for (FieldDefinition field : fields) {
            validateFieldRef(field);
            validateFieldRefLanguage(field);
        }
    }

    @Override
    protected void validateNameAndLanguage() {
        super.validateNameAndLanguage();

        BeanDefinition ancestor = getParent();
        Set<String> ancestors = new HashSet<>();

        while (ancestor != null && ancestor.getParent() != null) {
            if (ancestors.contains(ancestor.getLongName())) {
                return;
            }
            ancestors.add(ancestor.getLongName());
            ancestor = ancestor.getParent();
        }

        if (ancestor != null) {
            Set<String> ancestorLanguages = ancestor.getLanguages();
            if (ancestorLanguages.isEmpty()) {
                ancestor.validateNameAndLanguage();
            }
            languages.addAll(ancestorLanguages);
        }
    }

    protected void validateParent() {
        //目前只有配置支持
        if (category != Category.config) {
            return;
        }

        if (parentName == null) {
            if (delimiter == null) {
                delimiter = "_";
            }
            return;
        }

        if (delimiter != null) {
            addValidatedError(getValidatedName() + "的分隔符继承自父类，不支持自定义");
        }

        if (getLanguageStr() != null) {
            addValidatedError(getValidatedName() + "的语言约束继承自父类，不支持自定义");
        }

        BeanDefinition parent = getParent();
        if (parent == null) {
            addValidatedError(getValidatedName() + "的父" + getKindName() + "[" + parentName + "]不存在");
            return;
        }
        if (parent.getClass() != getClass()) {
            addValidatedError(getValidatedName() + "的父" + getKindName() + "[" + parentName + "]类型不合法");
        }

        parent.children.add(this);
        parent.dependentChildren.put(this.getLongName(), getName());

        BeanDefinition ancestor = parent;
        Set<String> ancestors = new HashSet<>();

        while (ancestor != null) {
            delimiter = ancestor.delimiter;

            if (ancestors.contains(ancestor.getLongName())) {
                addValidatedError(getValidatedName() + "和" + ancestors + "的父子关系不能有循环");
                return;
            }

            for (int i = ancestor.selfFields.size() - 1; i >= 0; i--) {
                FieldDefinition previousField = ancestor.selfFields.get(i).clone();
                previousField.setOwner(this);
                fields.add(0, previousField);
            }

            ancestors.add(ancestor.getLongName());
            ancestor.descendants.add(getLongName());
            ancestor = ancestor.getParent();
        }

        ancestor = parent;
        while (ancestor != null) {
            if (fields.size() > ancestor.descendantMaxFieldCount) {
                ancestor.descendantMaxFieldCount = fields.size();
            }
            ancestor = ancestor.getParent();
        }
    }

    @Override
    protected void validateDependents() {
        BeanDefinition parent = getParent();
        if (parent != null) {
            addDependent(DependentType.PARENT, this, this, parent);
        }

        for (FieldDefinition fieldDefinition : getFields()) {
            addDependent(DependentType.FIELD, this, fieldDefinition, fieldDefinition.getEnum());
            addDependent(DependentType.FIELD, this, fieldDefinition, fieldDefinition.getTypeBean());
            addDependent(DependentType.FIELD_VALUE, this, fieldDefinition, fieldDefinition.getValueTypeBean());
            if (fieldDefinition.isSimpleRef()) {
                addDependent(DependentType.FIELD_REF, this, fieldDefinition, fieldDefinition.getRefConfig());
            }
        }

        for (BeanDefinition child : getChildren()) {
            addDependent(DependentType.CHILD, this, this, child);
        }
    }

    @Override
    protected void validateField(FieldDefinition field) {
        super.validateField(field);

        validateFieldType(field);
        validateFieldRange(field);
        validateFieldBeanCycle(field);
        validateFieldId(field);
    }

    /**
     * /校验字段类型
     */
    protected void validateFieldType(FieldDefinition field) {
        if (field.getTypeInfo() == null) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型不能为空");
            return;
        }

        String[] fieldTypes = field.getTypeInfo().split("[:：]", -1);
        String fieldType = fieldTypes[0];

        if (fieldTypes.length == 1 && StringUtils.isBlank(fieldType)) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型不能为空");
            return;
        }

        field.setType(fieldType);
        if (!field.isLegalType()) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + fieldType + "]不合法");
            field.setType(null);
            return;
        }

        if (fieldTypes.length != 1 && !field.isCollectionType() && !(field.category == Category.message && (fieldType.equals("float") || fieldType.equals("double")))) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypeInfo() + "]格式错误");
            return;
        }

        if (fieldType.equals("list") || fieldType.equals("set")) {
            if (fieldTypes.length == 2 && !StringUtils.isBlank(fieldTypes[1])) {
                field.setValueType(fieldTypes[1]);
                if (!field.isLegalValueType()) {
                    addValidatedError(getValidatedName("的[") + field.getType() + "]类型" + field.getValidatedName() + "的值类型[" + field.getValueType() + "]不合法");
                }
            } else {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypeInfo() + "]格式错误，合法格式[" + fieldType + ":值类型]");
            }
        }

        if (fieldType.equals("map")) {
            if (fieldTypes.length == 3 && !StringUtils.isBlank(fieldTypes[1]) && !StringUtils.isBlank(fieldTypes[2])) {
                field.setKeyType(fieldTypes[1]);
                field.setValueType(fieldTypes[2]);
                if (!field.isPrimitiveKeyType()) {
                    addValidatedError(getValidatedName("的[") + field.getType() + "]类型" + field.getValidatedName() + "的键类型[" + field.getKeyType() + "]不合法");
                }
                if (!field.isLegalValueType()) {
                    addValidatedError(getValidatedName("的[") + field.getType() + "]类型" + field.getValidatedName() + "的值类型[" + field.getValueType() + "]不合法");
                }
            } else {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypeInfo() + "]格式错误，合法格式[" + fieldType + ":键类型:值类型]");
            }
        }

        if (field.category == Category.message && (fieldType.equals("float") || fieldType.equals("double"))) {
            boolean patternError = fieldTypes.length != 1 && fieldTypes.length != 2;
            if (fieldTypes.length == 2) {
                int scale = -1;
                try {
                    scale = Integer.parseInt(fieldTypes[1]);
                } catch (NumberFormatException e) {
                    patternError = true;
                }
                if (scale < 0 || scale > 15) {
                    patternError = true;
                }
                if (!patternError) {
                    field.setScale(scale);
                }
            }
            if (patternError) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypeInfo() + "]格式错误，合法格式[" + fieldType + "]或者[" + fieldType + ":精度(0-15)]");
            }
        }

    }

    /**
     * 校验字段数值范围限制
     */
    protected void validateFieldRange(FieldDefinition field) {
        Object min = field.getMin();
        Number minValue = null;

        if (min instanceof String) {
            if (field.isNumberType()) {
                try {
                    minValue = Long.parseLong((String) min);
                    field.setMin(minValue);
                } catch (NumberFormatException e1) {
                    try {
                        minValue = Double.parseDouble((String) min);
                        field.setMin(minValue);
                    } catch (NumberFormatException e2) {
                        addValidatedError(getValidatedName("的") + field.getValidatedName() + "最小值限制[" + min + "]不能为非数字");
                    }
                }
            } else {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]不支持最小值限制");
            }
        }

        Object max = field.getMax();
        Number maxValue = null;

        if (max instanceof String) {
            if (field.isNumberType()) {
                try {
                    maxValue = Long.parseLong((String) max);
                    field.setMax(maxValue);
                } catch (NumberFormatException e1) {
                    try {
                        maxValue = Double.parseDouble((String) max);
                        field.setMax(maxValue);
                    } catch (NumberFormatException e2) {
                        addValidatedError(getValidatedName("的") + field.getValidatedName() + "最大值限制[" + min + "]不能为非数字");
                    }
                }
            } else {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]不支持最大值限制");
            }
        }

        if (minValue != null && maxValue != null && Double.compare(minValue.doubleValue(), maxValue.doubleValue()) > 0) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "最小值限制[" + minValue + "]不能大于最大值限制[" + maxValue + "]");
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
            fieldBean = field.getTypeBean();
        } else if (field.isCollectionType()) {
            fieldBean = field.getValueTypeBean();
        }

        if (fieldBean == null) {
            return false;
        }

        if (fieldBeans.contains(fieldBean)) {
            addValidatedError(getValidatedName("的") + rootField.getValidatedName() + "循环依赖类型[" + fieldBean.getName() + "]");
            return true;
        }

        fieldBeans.add(fieldBean);

        for (FieldDefinition fieldBeanField : fieldBean.getFields()) {
            Set<BeanDefinition> fieldBeanFieldBeans = new HashSet<>(fieldBeans);
            if (validateFieldBeanCycle(rootField, fieldBeanField, fieldBeanFieldBeans)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 校验字段支持的语言
     */
    protected void validateFieldLanguage(FieldDefinition field) {
        try {
            field.getLanguages().addAll(Language.parse(languages, field.getLanguageStr()));
        } catch (IllegalArgumentException e) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "语言约束[" + field.getLanguageStr() + "]非法,合法语言类型" + languages);
        }
    }

    /**
     * 校验消息的字段ID
     */
    protected void validateFieldId(FieldDefinition field) {
        if (category != Category.message || field.getId() == null) {
            return;
        }

        int fieldId = -1;
        try {
            fieldId = Integer.parseInt(field.getId());
        } catch (NumberFormatException ignored) {
        }

        if (fieldId < 1 || fieldId > 63) {
            addValidatedError(getValidatedName() + "的字段ID[" + field.getId() + "]必须是[1-63]的整数");
        }

        if (!fieldIds.add(fieldId)) {
            addValidatedError(getValidatedName() + "的字段ID[" + field.getId() + "]必须不能重复");
        }
    }

    /**
     * 消息编解码是否兼容字段不一致的情况
     */
    public boolean isCompatible() {
        return !fieldIds.isEmpty();
    }

    public String getDelimiter() {
        return delimiter;
    }

    public BeanDefinition setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public String getEscapedDelimiter() {
        return ConfigDefinition.escapeDelimiter(delimiter);
    }

    /**
     * 校验字段引用，依赖索引校验结果
     */
    protected void validateFieldRef(FieldDefinition field) {
        if (field.getType() == null || getCategory() != Category.config || field.getRef() == null) {
            return;
        }

        if (!field.isMapType()) {
            String fieldRef = resolveFieldRef(field.getRef());
            int lastDotIndex = fieldRef.lastIndexOf(".");
            boolean refError = lastDotIndex < 0;

            if (lastDotIndex > 0) {
                String refConfig = fieldRef.substring(0, lastDotIndex);
                String refField = fieldRef.substring(lastDotIndex + 1);
                if (StringUtils.isBlank(refConfig) || StringUtils.isBlank(refField)) {
                    refError = true;
                } else {
                    validateFieldRef(field, false, refConfig, refField);
                }
            }

            if (refError) {
                addValidatedError(getValidatedName() + field.getValidatedName() + "的引用格式错误[" + field.getRef() + "]，正确格式:[(配置.)字段]");
            }

            return;
        }

        //map类型字段引用校验
        String[] fieldRefs = field.getRef().split("[:：]", -1);
        String mapRefErrorMsg = getValidatedName("的") + field.getValidatedName() + "类型[map]的引用格式错误[" + field.getRef() + "]，正确格式:[(键引用配置.)字段]或者[(键引用配置.)字段:(值引用配置.)字段]";
        if (fieldRefs.length != 1 && fieldRefs.length != 2) {
            addValidatedError(mapRefErrorMsg);
            return;
        }

        String keyRef = resolveFieldRef(fieldRefs[0]);
        int lastKeyDotIndex = keyRef.lastIndexOf(".");
        boolean refError = lastKeyDotIndex < 0;

        if (lastKeyDotIndex > 0) {
            String refKeyConfig = keyRef.substring(0, lastKeyDotIndex);
            String refKeyField = keyRef.substring(lastKeyDotIndex + 1);
            if (StringUtils.isBlank(refKeyConfig) || StringUtils.isBlank(refKeyField)) {
                refError = true;
            } else {
                validateFieldRef(field, true, refKeyConfig, refKeyField);
            }
        }

        if (fieldRefs.length == 2) {
            String valueRef = resolveFieldRef(fieldRefs[1]);
            int lastValueDotIndex = valueRef.lastIndexOf(".");
            if (lastValueDotIndex > 0) {
                String refValueConfig = valueRef.substring(1, lastValueDotIndex);
                String refValueField = valueRef.substring(lastValueDotIndex + 1);
                if (StringUtils.isBlank(refValueConfig) || StringUtils.isBlank(refValueField)) {
                    refError = true;
                } else {
                    validateFieldRef(field, false, refValueConfig, refValueField);
                }
            } else {
                refError = true;
            }
        }

        if (refError) {
            addValidatedError(mapRefErrorMsg);
        }
    }

    protected void validateFieldRef(FieldDefinition field, boolean keyType, String refConfigName, String refFiledName) {
        String refConfigAndField = refConfigName + "." + refFiledName;

        ConfigDefinition refConfig = parser.getConfig(this, refConfigName);
        if (refConfig == null) {
            addValidatedError(getValidatedName() + field.getValidatedName() + "的引用配置[" + refConfigName + "]不存在");
            return;
        }

        FieldDefinition refField = refConfig.getField(refFiledName);
        if (refField == null) {
            addValidatedError(getValidatedName() + field.getValidatedName() + "的引用字段[" + refConfigAndField + "]不存在");
            return;
        }

        if (refField == field) {
            addValidatedError(getValidatedName() + field.getValidatedName() + "不能引用自己");
            return;
        }

        for (String language : languages) {
            Set<String> refFieldLanguages = refField.getLanguages();
            if (!refFieldLanguages.contains(language)) {
                addValidatedError(getValidatedName() + "支持的语言范围" + languages + "必须小于或等于其" + field.getValidatedName() + "的引用[" + refConfigAndField + "]支持的语言范围" + refFieldLanguages);
                break;
            }
        }

        if (field.isCollectionType()) {
            if (keyType && field.isPrimitiveKeyType() && !field.getKeyType().equals(refField.getType()) && refField.getType() != null) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]的键类型[" + field.getKeyType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
            }
            if (!keyType && field.isLegalValueType() && !field.getValueType().equals(refField.getType()) && refField.getType() != null) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]的值类型[" + field.getValueType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
            }
        } else if (!field.getType().equals(refField.getType()) && refField.getType() != null) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "的类型[" + field.getType() + "]和引用字段[" + refConfigAndField + "]的类型[" + refField.getType() + "]不一致");
        }

        IndexDefinition refFieldIndex = refConfig.getIndexByField1(refField);
        if (refFieldIndex == null) {
            addValidatedError(getValidatedName() + field.getValidatedName() + "的引用字段[" + refConfigAndField + "]不是一级索引");
        }
    }

    /**
     * 校验字段依赖语言
     */
    protected void validateFieldRefLanguage(FieldDefinition field) {
        if (getCategory() != Category.config) {
            return;
        }

        BeanDefinition fieldBean = null;
        if (field.isBeanType()) {
            fieldBean = field.getTypeBean();
        } else if (field.isCollectionType()) {
            fieldBean = field.getValueTypeBean();
        }
        if (fieldBean == null) {
            return;
        }
        if (!fieldBean.languages.containsAll(this.languages)) {
            addValidatedError(getValidatedName() + "支持的语言范围" + languages + "必须小于或等于其依赖" + fieldBean.getValidatedName() + "支持的语言范围" + fieldBean.languages);
        }
    }

    private void validateDelimiter() {
        if (category != Category.config || getClass() != BeanDefinition.class) {
            return;
        }

        if (delimiter.length() != 1) {
            addValidatedError(getValidatedName() + "的分隔符[" + delimiter + "]长度必须1个字符");
        } else if (!Constants.LEGAL_DELIMITERS.contains(delimiter)) {
            addValidatedError(getValidatedName() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
        } else {
            List<String> delimiters = new ArrayList<>();
            validateDelimiter(this, delimiters);

            if (delimiters.size() != new HashSet<>(delimiters).size()) {
                addValidatedError(getValidatedName() + "关联分隔符有重复[" + String.join("", delimiters) + "]");
            }
        }
    }

    private void validateDelimiter(BeanDefinition beanDefinition, List<String> delimiters) {
        String delimiter = beanDefinition.delimiter;
        for (int i = 0; i < delimiter.length(); i++) {
            delimiters.add(String.valueOf(delimiter.charAt(i)));
        }
        for (FieldDefinition beanField : beanDefinition.getFields()) {
            validateFieldDelimiter(beanField, delimiters);
        }
    }

    protected void validateFieldDelimiter(FieldDefinition field, List<String> delimiters) {
        if (field.isCycle() || !field.isCollectionType()) {
            return;
        }

        String delimiter = field.getDelimiter();

        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            delimiters.add(s);
            if (!Constants.LEGAL_DELIMITERS.contains(s)) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
            }
        }

        int charNumError = 0;
        if (delimiter.length() != 1 && (field.isListType() || field.isSetType())) {
            charNumError = 1;
        }
        if (field.isMapType()) {
            if (delimiter.length() != 2) {
                charNumError = 2;
            } else if (delimiter.charAt(0) == delimiter.charAt(1)) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[map]的分隔符[" + delimiter + "]必须是2个不同的字符");
            }
        }
        if (charNumError > 0) {
            addValidatedError(getValidatedName() + "[" + field.getType() + "]类型字段" + field.getValidatedName() + "的分隔符[" + delimiter + "]必须是" + charNumError + "个字符");
        }

        BeanDefinition fieldValueBean = field.getValueTypeBean();
        if (fieldValueBean != null) {
            validateDelimiter(fieldValueBean, delimiters);
        }
    }

}
