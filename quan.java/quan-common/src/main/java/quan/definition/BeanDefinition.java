package quan.definition;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import quan.definition.DependentSource.DependentType;
import quan.definition.config.ConfigDefinition;

import java.util.*;

/**
 * Bean定义，被数据、消息和配置共用
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends ClassDefinition {

    //配置的父类
    protected String parentName;

    //和具体语言相关的父类名，可能会包含包名
    protected String parentClassName;

    //配置的所有后代类
    protected Set<String> descendantLongNames = new HashSet<>();

    private TreeSet<String> meAndDescendantLongNames = new TreeSet<>();

    private TreeSet<String> meAndDescendantShortNames = new TreeSet<>();

    //配置的所有子类
    protected Set<BeanDefinition> children = new HashSet<>();

    //和语言无关的带包子类名:和语言相关的完整子类名
    protected Map<String,String> dependentChildren = new HashMap<>();

    protected int descendantMaxFieldCount;

    protected List<FieldDefinition> selfFields = new ArrayList<>();

    //配置Bean的字段分隔符
    private String delimiter = "_";

    private String escapedDelimiter;

    //消息的字段ID
    private Set<Integer> fieldIds = new HashSet<>();

    public BeanDefinition() {
    }

    public BeanDefinition(String parent, String delimiter) {
        if (!StringUtils.isEmpty(parent)) {
            this.parentName = parent;
        }
        if (!StringUtils.isEmpty(delimiter)) {
            this.delimiter = delimiter;
        }
    }

    @Override
    public int getKind() {
        return 2;
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
            return getShortClassName(parentName);
        }
        return parentClassName;
    }

    public String getWholeParentName() {
        return getLongClassName(this, parentName);
    }

    public BeanDefinition getParent() {
        return parser.getBean(getWholeParentName());
    }

    public Set<BeanDefinition> getChildren() {
        return children;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public TreeSet<String> getMeAndDescendantLongNames() {
        if (meAndDescendantLongNames.isEmpty()) {
            meAndDescendantLongNames.addAll(descendantLongNames);
            meAndDescendantLongNames.add(getLongName());
        }
        return meAndDescendantLongNames;
    }

    public TreeSet<String> getMeAndDescendantShortNames() {
        if (meAndDescendantShortNames.isEmpty()) {
            for (String meAndDescendantLongName : getMeAndDescendantLongNames()) {
                meAndDescendantShortNames.add(getShortClassName(meAndDescendantLongName));
            }
        }
        return meAndDescendantShortNames;
    }

    public int getDescendantMaxFieldCount() {
        return descendantMaxFieldCount == 0 ? fields.size() : descendantMaxFieldCount;
    }

    public Map<String,String> getDependentChildren() {
        return dependentChildren;
    }

    @Override
    public void validate1() {
        super.validate1();
        validateDelimiter();

        if (!fieldIds.isEmpty() && fieldIds.size() != fields.size()) {
            addValidatedError(getValidatedName("的") + "所有字段必须要同时定义ID或者同时不定义ID");
        }
    }

    @Override
    public void validate2() {
        super.validate2();
        validateParent();
    }

    @Override
    public void validate3() {
        super.validate3();
        for (FieldDefinition field : fields) {
            //校验字段引用
            validateFieldRef(field);
        }
    }

    protected void validateParent() {
        //目前只有配置支持
        if (category != Category.config) {
            return;
        }

        if (getParentName() == null) {
            return;
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
        parent.dependentChildren.put(this.getLongName(),getName());

        Set<String> ancestors = new HashSet<>();
        while (parent != null) {
            if (ancestors.contains(parent.getLongName())) {
                addValidatedError(getValidatedName() + "和" + ancestors + "的父子关系不能有循环");
                return;
            }
            ancestors.add(parent.getLongName());

            for (int i = parent.selfFields.size() - 1; i >= 0; i--) {
                FieldDefinition parentField = parent.selfFields.get(i).clone();
                parentField.setOwner(this);
                fields.add(0, parentField);
            }

            parent.descendantLongNames.add(getLongName());
            parent = parent.getParent();
        }

        parent = getParent();
        while (parent != null) {
            if (fields.size() > parent.descendantMaxFieldCount) {
                parent.descendantMaxFieldCount = fields.size();
            }
            parent = parent.getParent();
        }
    }

    @Override
    protected void validateField(FieldDefinition field) {
        super.validateField(field);

        //校验字段类型
        validateFieldType(field);

        //校验字段循环依赖
        validateFieldBeanCycle(field);

        //校验字段依赖语言
        validateFieldBeanLanguage(field);

        //校验消息的字段ID
        validateFieldId(field);
    }

    protected void validateFieldType(FieldDefinition field) {
        if (field.getTypes() == null) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型不能为空");
            return;
        }

        String[] fieldTypes = field.getTypes().split(":", -1);
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
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypes() + "]格式错误");
            return;
        }

        if (fieldType.equals("list") || fieldType.equals("set")) {
            if (fieldTypes.length == 2 && !StringUtils.isBlank(fieldTypes[1])) {
                field.setValueType(fieldTypes[1]);
                if (!field.isLegalValueType()) {
                    addValidatedError(getValidatedName("的[") + field.getType() + "]类型" + field.getValidatedName() + "的值类型[" + field.getValueType() + "]不合法");
                }
            } else {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypes() + "]格式错误，合法格式[" + fieldType + ":值类型]");
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
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypes() + "]格式错误，合法格式[" + fieldType + ":键类型:值类型]");
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
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getTypes() + "]格式错误，合法格式[" + fieldType + "]或者[" + fieldType + ":精度(0-15)]");
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

    protected void validateFieldBeanLanguage(FieldDefinition field) {
        if (category == Category.data) {
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
        if (!fieldBean.getSupportedLanguages().containsAll(getSupportedLanguages())) {
            addValidatedError(getValidatedName() + "支持的语言范围" + supportedLanguages + "必须小于或等于其依赖" + fieldBean.getValidatedName() + "支持的语言范围" + fieldBean.supportedLanguages);
        }
    }

    @Override
    protected void validateDependents() {
        BeanDefinition parent = getParent();
        if (parent != null) {
            addDependent(DependentType.PARENT, this, this, parent);
        }

        for (FieldDefinition fieldDefinition : getFields()) {
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
            addValidatedError(getValidatedName("的") + "字段ID[" + field.getId() + "]必须是[1-63]的整数");
        }

        if (!fieldIds.add(fieldId)) {
            addValidatedError(getValidatedName("的") + "字段ID[" + field.getId() + "]必须不能重复");
        }
    }

    public boolean isDefinedFieldId() {
        return !fieldIds.isEmpty();
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
        if (StringUtils.isEmpty(escapedDelimiter)) {
            String delimiter_ = this.delimiter;
            BeanDefinition parentBean = getParent();
            while (parentBean != null) {
                delimiter_ = parentBean.delimiter;
                parentBean = parentBean.getParent();
            }
            escapedDelimiter = ConfigDefinition.escapeDelimiter(delimiter_);
        }
        return escapedDelimiter;
    }

    private void validateDelimiter() {
        if (category != Category.config || getClass() != BeanDefinition.class) {
            return;
        }

        if (delimiter.length() != 1) {
            addValidatedError(getValidatedName() + "的分隔符[" + delimiter + "]长度必须1个字符");
        }
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (!Constants.LEGAL_DELIMITERS.contains(s)) {
                addValidatedError(getValidatedName() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
            }
        }
    }

    protected void validateFieldRef(FieldDefinition field) {
        if (field.getType() == null || getCategory() != Category.config || field.getRef() == null) {
            return;
        }

        if (!field.isMapType()) {
            int lastDotIndex = field.getRef().lastIndexOf(".");
            boolean refError = lastDotIndex < 0;

            if (lastDotIndex > 0) {
                String refConfig = field.getRef().substring(0, lastDotIndex);
                String refField = field.getRef().substring(lastDotIndex + 1);
                if (StringUtils.isBlank(refConfig) || StringUtils.isBlank(refField)) {
                    refError = true;
                } else {
                    validateFieldRef(field, false, refConfig, refField);
                }
            }

            if (refError) {
                addValidatedError(getValidatedName() + field.getValidatedName() + "的引用格式错误[" + field.getRef() + "]，正确格式:[配置.字段]");
            }

            return;
        }

        //map类型字段引用校验
        String[] fieldRefs = field.getRef().split(":", -1);
        String mapRefErrorMsg = getValidatedName("的") + field.getValidatedName() + "类型[map]的引用格式错误[" + field.getRef() + "]，正确格式:[键引用的配置.字段]或者[键引用配置.字段:值引用的配置.字段]";
        if (fieldRefs.length != 1 && fieldRefs.length != 2) {
            addValidatedError(mapRefErrorMsg);
            return;
        }

        int lastKeyDotIndex = fieldRefs[0].lastIndexOf(".");
        boolean refError = lastKeyDotIndex < 0;

        if (lastKeyDotIndex > 0) {
            String refKeyConfig = fieldRefs[0].substring(0, lastKeyDotIndex);
            String refKeyField = fieldRefs[0].substring(lastKeyDotIndex + 1);
            if (StringUtils.isBlank(refKeyConfig) || StringUtils.isBlank(refKeyField)) {
                refError = true;
            } else {
                validateFieldRef(field, true, refKeyConfig, refKeyField);
            }
        }

        if (fieldRefs.length == 2) {
            int lastValueDotIndex = fieldRefs[1].lastIndexOf(".");
            if (lastValueDotIndex > 0) {
                String refValueConfig = fieldRefs[1].substring(1, lastValueDotIndex);
                String refValueField = fieldRefs[1].substring(lastKeyDotIndex + 1);
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

    protected String getRefConfigName(String refConfigName) {
        if (!refConfigName.contains(".")) {
            return getPackageName() + "." + refConfigName;
        }
        return refConfigName;
    }

    protected void validateFieldRef(FieldDefinition field, boolean keyType, String refConfigName, String refFiledName) {
        String refConfigAndField = refConfigName + "." + refFiledName;

        ConfigDefinition refConfig = parser.getConfig(getRefConfigName(refConfigName));
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

        if (!refConfig.getSupportedLanguages().containsAll(getSupportedLanguages())) {
            addValidatedError(getValidatedName() + "支持的语言范围" + supportedLanguages + "必须小于或等于其引用" + refConfig.getValidatedName() + "支持的语言范围" + refConfig.supportedLanguages);
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

}
