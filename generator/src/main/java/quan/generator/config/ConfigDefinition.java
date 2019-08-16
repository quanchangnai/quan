package quan.generator.config;

import org.apache.commons.lang3.StringUtils;
import quan.generator.*;

import java.util.*;
import java.util.regex.Pattern;

/**
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

    private List<IndexDefinition> indexes = new ArrayList<>();

    private List<IndexDefinition> selfIndexes = new ArrayList<>();

    protected List<FieldDefinition> selfFields = new ArrayList<>();

    private Map<String, FieldDefinition> columnFields = new HashMap<>();

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
    public void setName(String name) {
        super.setName(name);
        if (table == null) {
            table = getName();
        }
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
            allTables.addAll(Arrays.asList(configDefinition.table.split(",")));
        }

        return allTables;
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

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexes.add(indexDefinition);
        selfIndexes.add(indexDefinition);
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


    @Override
    public void validate() {
        if (getName() == null) {
            addValidatedError(getName4Validate() + "的名字不能为空");
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            addValidatedError(getName4Validate() + "的语言类型" + languages + "非法,合法语言类型" + Language.names());
        }

        if (table == null) {
            table = getName();
        }
        if (StringUtils.isBlank(getComment())) {
            setComment(table);
        }

        //支持分表
        tables.addAll(Arrays.asList(table.split(",")));
        for (String t : tables) {
            ConfigDefinition other = parser.getTableConfigs().get(t);
            if (other != null) {
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

        //支持子表
        ConfigDefinition parentConfig = getParentConfig();
        if (parentConfig == null) {
            addValidatedError(getName4Validate() + "的父配置[" + parent + "]不存在");
            return;
        }

        Set<String> parents = new HashSet<>();
        while (parentConfig != null) {
            if (parents.contains(parentConfig.getName())) {
                addValidatedError(getName4Validate() + "和父子关系" + parents + "不能有循环");
                return;
            }
            parents.add(parentConfig.getName());
            parentConfig = parentConfig.getParentConfig();
        }

        parentConfig = getParentConfig();
        while (parentConfig != null) {
            fields.addAll(0, parentConfig.selfFields);
            indexes.addAll(0, parentConfig.selfIndexes);
            parentConfig.descendants.add(getName());
            parentConfig = parentConfig.getParentConfig();
        }

    }

    @Override
    protected void validateField(FieldDefinition field) {
        if (field.getColumn() != null) {
            if (!columnFields.containsKey(field.getColumn())) {
                columnFields.put(field.getColumn(), field);
            } else {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "和列[" + field.getColumn() + "]必须一一对应");
            }
        }

        if (field.getName() != null) {
            if (!nameFields.containsKey(field.getName())) {
                nameFields.put(field.getName(), field);
            } else {
                addValidatedError(getName4Validate("的") + "字段名[" + field.getName() + "]不能重复");
            }
        }
    }

    protected void validateSelfField(FieldDefinition field) {
        //校验字段名
        if (field.getName() == null) {
            addValidatedError(getName4Validate("的") + "字段名不能为空");
            return;
        }

        //校验字段类型
        validateFieldType(field);

        //校验字段循环依赖
        validateFieldBeanCycle(field);

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
            if (!Constants.legalDelimiters.contains(s)) {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.legalDelimiters);
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
            validateFieldRef(selfField);
        }
    }

    protected boolean supportFieldRef() {
        return true;
    }

    private void validateIndexes() {
        for (FieldDefinition selfField : selfFields) {
            if (selfField.getIndex() == null && !selfField.getName().equals("id")) {
                continue;
            }
            if (!selfField.isPrimitiveType() && !selfField.isEnumType()) {
                addValidatedError(getName4Validate("的") + selfField.getName4Validate() + "类型[" + selfField.getType() + "]不支持索引，允许的类型为" + Constants.primitiveTypes + "或枚举");
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
        } else if (!Pattern.matches(indexDefinition.namePattern(), indexDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "索引名[" + indexDefinition.getName() + "]格式错误");
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

        String[] fieldNameArray = fieldNames.split(",");
        if (fieldNameArray.length > 3) {
            addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldNames + "]不能超过三个");
        }
        for (String fieldName : fieldNameArray) {
            FieldDefinition fieldDefinition = nameFields.get(fieldName);
            if (fieldDefinition == null) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldName + "]不存在");
                continue;
            }
            if (!fieldDefinition.isPrimitiveType() && !fieldDefinition.isEnumType()) {
                addValidatedError(getName4Validate("的") + indexDefinition.getName4Validate() + "字段[" + fieldName + "]类型[" + fieldDefinition.getType() + "]非法，允许的类型为" + Constants.primitiveTypes + "或枚举");
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
            if (Constants.needEscapeChars.contains(s)) {
                escapedDelimiter.append("\\");
            }
            escapedDelimiter.append(s);
        }
        return escapedDelimiter.toString();
    }

}
