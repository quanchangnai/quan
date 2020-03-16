package quan.definition.config;

import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.Constants;
import quan.definition.DefinitionCategory;
import quan.definition.FieldDefinition;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 配置定义
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends BeanDefinition {

    //配置本身对应的表，有分表以逗号分格
    private String table;

    //配置对应的表，包含分表
    private List<String> tables = new ArrayList<>();

    //配置对应的表，包含分表和子表
    private List<String> allTables = new ArrayList<>();

    //配置的父类
    private String parent;

    //配置的所有后代类
    private Set<String> descendants = new HashSet<>();

    //配置的所有子类
    private Set<ConfigDefinition> children = new HashSet<>();

    //所有索引,包含继承下来的索引
    private List<IndexDefinition> indexes = new ArrayList<>();

    private List<IndexDefinition> selfIndexes = new ArrayList<>();

    protected List<FieldDefinition> selfFields = new ArrayList<>();

    //列名:字段
    private Map<String, FieldDefinition> columnFields = new HashMap<>();

    private List<String> rows = new ArrayList<>();

    private Set<ConstantDefinition> constantDefinitions = new HashSet<>();

    {
        category = DefinitionCategory.config;
    }

    public ConfigDefinition() {
    }

    public ConfigDefinition(String table, String parent) {
        if (!StringUtils.isBlank(table)) {
            this.table = table;
        }
        if (!StringUtils.isBlank(parent)) {
            this.parent = parent;
        }
    }

    @Override
    public ConfigDefinition setCategory(DefinitionCategory category) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDefinitionType() {
        return 6;
    }

    @Override
    public String getDefinitionTypeName() {
        return "配置";
    }

    @Override
    protected Pattern namePattern() {
        return Constants.CONFIG_NAME_PATTERN;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (table == null) {
            table = getName();
        }
    }

    public ConfigDefinition setParent(String parent) {
        if (StringUtils.isBlank(parent)) {
            return this;
        }
        this.parent = parent.trim();
        return this;
    }

    public String getParent() {
        return parent;
    }

    public ConfigDefinition getParentConfig() {
        return parser.getConfig(getParent());
    }

    public Set<ConfigDefinition> getChildren() {
        return children;
    }

    public Set<String> getDescendants() {
        return descendants;
    }

    public Set<String> getDescendantsAndMe() {
        Set<String> descendantsAndMe = new HashSet<>(descendants);
        descendantsAndMe.add(getName());
        return descendantsAndMe;
    }

    public ConfigDefinition setTable(String table) {
        if (StringUtils.isBlank(table)) {
            return this;
        }
        this.table = table;
        return this;
    }

    public String getTable() {
        return table;
    }

    public List<String> getTables() {
        return tables;
    }

    /**
     * 自身和子类的所有表
     */
    public List<String> getAllTables() {
        if (!allTables.isEmpty()) {
            return allTables;
        }

        Set<String> childrenAndMe = new HashSet<>(descendants);
        childrenAndMe.add(getName());

        for (String configName : childrenAndMe) {
            ConfigDefinition configDefinition = parser.getConfig(configName);
            if (configDefinition == null) {
                continue;
            }
            allTables.addAll(Arrays.asList(configDefinition.table.split(",", -1)));
        }

        return allTables;
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexes.add(indexDefinition);
        selfIndexes.add(indexDefinition);
    }

    public boolean isSelfIndex(IndexDefinition indexDefinition) {
        return selfIndexes.contains(indexDefinition);
    }

    public IndexDefinition getIndexByField1(FieldDefinition field1) {
        for (IndexDefinition index : indexes) {
            if (index.getFields().get(0) == field1) {
                return index;
            }
        }
        return null;
    }

    public boolean isIndexField(FieldDefinition field) {
        for (IndexDefinition index : indexes) {
            if (index.getFields().contains(field)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, FieldDefinition> getColumnFields() {
        return columnFields;
    }


    @Override
    public void addField(FieldDefinition fieldDefinition) {
        super.addField(fieldDefinition);
        selfFields.add(fieldDefinition);
    }


    public List<FieldDefinition> getSelfFields() {
        return selfFields;
    }


    public Set<ConstantDefinition> getConstantDefinitions() {
        return constantDefinitions;
    }

    public boolean isConstantKeyField(FieldDefinition fieldDefinition) {
        ConfigDefinition parentConfig = getParentConfig();
        if (parentConfig != null && parentConfig.isConstantKeyField(fieldDefinition)) {
            return true;
        }
        for (ConstantDefinition constantDefinition : constantDefinitions) {
            if (constantDefinition.getKeyField() == fieldDefinition) {
                return true;
            }
        }
        return false;
    }

    public List<String> getRows() {
        return rows;
    }

    public ConfigDefinition setRows(List<String> rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public void validate() {
        validateNameAndLanguage();

        if (table == null) {
            table = getName();
        }
        if (StringUtils.isBlank(getComment())) {
            setComment(table);
        }

        //支持分表
        tables.addAll(Arrays.asList(table.split(",", -1)));
        for (String t : tables) {
            ConfigDefinition other = parser.getTableConfigs().get(t);
            if (other != null && !getName().equals(other.getName())) {
                addValidatedError(getName4Validate() + other.getName4Validate() + "和表格[" + t + "]不能多对一", other);
            }
            parser.getTableConfigs().put(t, this);
        }

        validateParent();

        for (FieldDefinition field : fields) {
            validateField(field);
        }

        for (FieldDefinition selfField : selfFields) {
            validateSelfField(selfField);
        }

        validateIndexes();
    }

    private void validateParent() {
        if (getParent() == null) {
            return;
        }

        ConfigDefinition parentConfig = getParentConfig();
        if (parentConfig == null) {
            addValidatedError(getName4Validate() + "的父配置[" + parent + "]不存在");
            return;
        }

        if (!parentConfig.getSupportedLanguages().containsAll(getSupportedLanguages())) {
            addValidatedError(getName4Validate() + "支持的语言范围" + supportedLanguages + "必须小于或等于其父配置[" + parent + "]所支持的语言范围" + parentConfig.supportedLanguages);
        }

        parentConfig.children.add(this);

        Set<String> ancestors = new HashSet<>();
        while (parentConfig != null) {
            if (ancestors.contains(parentConfig.getName())) {
                addValidatedError(getName4Validate() + "和父子关系" + ancestors + "不能有循环");
                return;
            }

            fields.addAll(0, parentConfig.selfFields);
            indexes.addAll(0, parentConfig.selfIndexes);
            parentConfig.descendants.add(getName());
            ancestors.add(parentConfig.getName());

            parentConfig = parentConfig.getParentConfig();
        }
    }

    @Override
    protected void validateField(FieldDefinition field) {
        validateFieldNameDuplicate(field);

        if (field.getColumn() != null) {
            if (!columnFields.containsKey(field.getColumn())) {
                columnFields.put(field.getColumn(), field);
            } else {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "和列[" + field.getColumn() + "]必须一一对应");
            }
        }
    }

    protected void validateSelfField(FieldDefinition field) {
        //校验字段名
        validateFieldNameSelf(field);

        //校验字段类型
        validateFieldType(field);

        //校验字段循环依赖
        validateFieldBeanCycle(field);

        //校验字段依赖语言
        validateFieldBeanLanguage(field);

        if (field.getColumn() == null) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "对应的列不能为空");
            return;
        }

        if (StringUtils.isBlank(field.getComment())) {
            field.setComment(field.getColumn());
        }

        //校验字段的分隔符
        validateFieldDelimiter(field);
    }

    private void validateFieldLanguage(FieldDefinition field) {
        if (isIndexField(field) && (field.isExcludeLanguage() || !field.getLanguages().isEmpty())) {
            addValidatedError(getName4Validate("的索引") + field.getName4Validate() + "不支持设置语言");
            return;
        }

        getSupportedLanguages();
        Set<String> fieldIllegalLanguages = new HashSet<>();

        for (String fieldLanguage : field.getLanguages()) {
            if (!supportedLanguages.contains(fieldLanguage)) {
                fieldIllegalLanguages.add(fieldLanguage);
            }
        }

        if (!fieldIllegalLanguages.isEmpty()) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "支持的语言类型" + fieldIllegalLanguages + "非法,合法语言类型" + supportedLanguages);
        }
    }

    private void validateFieldDelimiter(FieldDefinition field) {
        if (field.isCycle()) {
            return;
        }

        ArrayList<String> delimiterList = new ArrayList<>();
        validateFieldDelimiter(field, delimiterList);

        Set<String> delimiterSet = new HashSet<>(delimiterList);
        if (delimiterList.size() != delimiterSet.size()) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "关联分隔符有重复[" + String.join("", delimiterList) + "]");
        }
    }

    private void validateFieldDelimiter(FieldDefinition field, List<String> delimiters) {
        if (field.isBeanType()) {
            validateBeanDelimiter(field.getBean(), delimiters);
            return;
        }

        if (!field.isCollectionType()) {
            return;
        }

        String delimiter = field.getDelimiter();

        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            delimiters.add(s);
            if (!Constants.LEGAL_DELIMITERS.contains(s)) {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
            }
        }

        int charNumError = 0;
        if (delimiter.length() != 1 && (field.isBeanType() || field.getType().equals("list") || field.getType().equals("set"))) {
            charNumError = 1;
        }
        if (field.getType().equals("map")) {
            if (delimiter.length() != 2) {
                charNumError = 2;
            } else if (delimiter.charAt(0) == delimiter.charAt(1)) {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "类型[map]的分隔符[" + delimiter + "]必须是2个不相同的字符");
            }
        }
        if (charNumError > 0) {
            addValidatedError(getName4Validate() + "[" + field.getType() + "]类型字段" + field.getName4Validate() + "的分隔符[" + delimiter + "]必须是" + charNumError + "个字符");
        }

        BeanDefinition fieldValueBean = parser.getBean(field.getValueType());
        if (fieldValueBean != null) {
            validateBeanDelimiter(fieldValueBean, delimiters);
        }
    }

    private void validateBeanDelimiter(BeanDefinition beanDefinition, List<String> delimiters) {
        String delimiter = beanDefinition.getDelimiter();
        for (int i = 0; i < delimiter.length(); i++) {
            delimiters.add(String.valueOf(delimiter.charAt(i)));
        }
        for (FieldDefinition beanField : beanDefinition.getFields()) {
            validateFieldDelimiter(beanField, delimiters);
        }
    }

    @Override
    public void validate2() {
        for (FieldDefinition selfField : selfFields) {
            //校验字段引用
            validateFieldRef(selfField);
            //校验字段支持的语言
            validateFieldLanguage(selfField);
        }
    }

    private void validateIndexes() {
        for (FieldDefinition selfField : selfFields) {
            if (selfField.getIndex() == null && !selfField.getName().equals("id")) {
                continue;
            }
            if (!selfField.isPrimitiveType() && !selfField.isEnumType()) {
                addValidatedError(getName4Validate("的") + selfField.getName4Validate() + "类型[" + selfField.getType() + "]不支持索引，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
                continue;
            }
            IndexDefinition indexDefinition = new IndexDefinition();
            indexDefinition.setParser(parser);
            indexDefinition.setCategory(category);

            indexDefinition.setName(selfField.getName());
            indexDefinition.setFieldNames(selfField.getName());
            if (selfField.getName().equals("id")) {
                indexDefinition.setType("unique");
            } else {
                indexDefinition.setType(selfField.getIndex());
            }
            if (selfField.getComment() == null) {
                indexDefinition.setComment(selfField.getColumn());
            } else {
                indexDefinition.setComment(selfField.getComment());
            }
            addIndex(indexDefinition);
        }

        if (indexes.isEmpty()) {
            addValidatedError(getName4Validate() + "至少需要一个索引");
            return;
        }
        for (IndexDefinition indexDefinition : selfIndexes) {
            validateIndex(indexDefinition);
        }
        Set<String> indexNames = new HashSet<>();
        for (IndexDefinition indexDefinition : indexes) {
            if (indexNames.contains(indexDefinition.getName())) {
                addValidatedError(getName4Validate() + "的索引名[" + indexDefinition.getName() + "]重复");
                continue;
            }
            indexNames.add(indexDefinition.getName());
        }
    }

    private void validateIndex(IndexDefinition indexDefinition) {
        if (indexDefinition.getName() == null) {
            addValidatedError(getName4Validate() + "的索引名不能为空");
        } else if (!indexDefinition.namePattern().matcher(indexDefinition.getName()).matches()) {
            addValidatedError(getName4Validate("的") + "索引名[" + indexDefinition.getName() + "]格式错误,正确格式:" + indexDefinition.namePattern());
        }

        String indexType = indexDefinition.getType();
        if (indexType == null) {
            addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "类型不能为空");
        } else {
            List<String> allowIndexTypes = Arrays.asList("normal", "n", "unique", "u");
            if (!allowIndexTypes.contains(indexType)) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "类型[" + indexType + "]非法,允许类型" + allowIndexTypes);
            }
        }

        String fieldNames = indexDefinition.getFieldNames();
        if (fieldNames == null) {
            addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段不能为空");
            return;
        }

        String[] fieldNameArray = fieldNames.split(",", -1);

        boolean fieldNamePatternError = false;
        for (String fieldName : fieldNameArray) {
            if (!fieldNamePatternError) {
                fieldNamePatternError = StringUtils.isBlank(fieldName);
            }
        }

        if (fieldNamePatternError) {
            addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldNames + "]格式错误");
        } else if (fieldNameArray.length > 3) {
            addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldNames + "]不能超过三个");
        }

        for (String fieldName : fieldNameArray) {
            if (StringUtils.isBlank(fieldName)) {
                continue;
            }
            FieldDefinition fieldDefinition = nameFields.get(fieldName);
            if (fieldDefinition == null) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldName + "]不存在");
                continue;
            }
            if (!fieldDefinition.isPrimitiveType() && !fieldDefinition.isEnumType()) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldName + "]类型[" + fieldDefinition.getType() + "]非法，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
            }
            if (!indexDefinition.addField(fieldDefinition)) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldNames + "]不能重复");
            }
        }

    }

    /**
     * 转义分隔符里的正则表达式特殊字符
     */
    public static String escapeDelimiter(String delimiter) {
        StringBuilder escapedDelimiter = new StringBuilder();
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (i > 0) {
                escapedDelimiter.append("|");
            }
            if (Constants.NEED_ESCAPE_DELIMITERS.contains(s)) {
                escapedDelimiter.append("\\");
            }
            escapedDelimiter.append(s);
        }
        return escapedDelimiter.toString();
    }

}
