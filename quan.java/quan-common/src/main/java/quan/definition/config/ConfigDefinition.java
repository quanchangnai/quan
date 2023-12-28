package quan.definition.config;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import quan.definition.*;
import quan.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 配置定义
 */
public class ConfigDefinition extends BeanDefinition {

    //配置本身对应的表，有分表以逗号分格
    private String table;

    //配置对应的表，包含分表
    private final TreeSet<String> tables = new TreeSet<>();

    //配置对应的表，包含分表和子表
    private final TreeSet<String> allTables = new TreeSet<>();

    //所有索引,包含继承下来的索引
    private final List<IndexDefinition> indexes = new LinkedList<>();

    //仅自己的索引
    private final List<IndexDefinition> selfIndexes = new LinkedList<>();

    //列名:字段
    private final Map<String, FieldDefinition> columnFields = new HashMap<>();

    //校验表达式(OGNL)
    private Set<Object> validations = new LinkedHashSet<>();

    private List<String> rows = new ArrayList<>();

    //配置数据版本
    private String version2;

    private final Set<ConstantDefinition> constantDefinitions = new HashSet<>();

    {
        category = Category.config;
    }

    public static final Set<String> illegalNames = CollectionUtils.asSet("Field", "self");

    public ConfigDefinition() {
    }

    public ConfigDefinition(String table, String parent) {
        if (!StringUtils.isBlank(table)) {
            this.table = table;
        }
        if (!StringUtils.isBlank(parent)) {
            this.parentName = parent;
        }
    }

    @Override
    public ConfigDefinition setCategory(Category category) {
        if (category != this.category) {
            throw new IllegalStateException();
        }
        return this;
    }

    @Override
    public int getKind() {
        return KIND_CONFIG;
    }

    @Override
    public String getKindName() {
        return "配置";
    }

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getConfigNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
    }

    @Override
    public ConfigDefinition getParent() {
        return parser.getConfig(this, parentName);
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

    public TreeSet<String> getTables() {
        return tables;
    }


    /**
     * 自身和子类的所有表
     */
    public TreeSet<String> getAllTables() {
        if (!allTables.isEmpty()) {
            return allTables;
        }

        for (String configLongName : getMeAndDescendants()) {
            ConfigDefinition configDefinition = parser.getConfig(configLongName);
            if (configDefinition == null) {
                continue;
            }
            allTables.addAll(Arrays.asList(configDefinition.table.split("[,，]", -1)));
        }

        return allTables;
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public List<IndexDefinition> getSelfIndexes() {
        return selfIndexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexDefinition.setOwnerDefinition(this);
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

    public Set<ConstantDefinition> getConstantDefinitions() {
        return constantDefinitions;
    }

    public boolean isConstantKeyField(FieldDefinition fieldDefinition) {
        ConfigDefinition parent = getParent();
        if (parent != null && parent.isConstantKeyField(fieldDefinition)) {
            return true;
        }
        for (ConstantDefinition constantDefinition : constantDefinitions) {
            if (constantDefinition.getKeyField() == fieldDefinition) {
                return true;
            }
        }
        return false;
    }

    public boolean isConstantKeyField(String fieldName) {
        ConfigDefinition parentConfig = getParent();
        if (parentConfig != null && parentConfig.isConstantKeyField(fieldName)) {
            return true;
        }
        for (ConstantDefinition constantDefinition : constantDefinitions) {
            if (constantDefinition.getKeyField().getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public Set<Object> getValidations() {
        return validations;
    }

    public List<String> getRows() {
        return rows;
    }

    public ConfigDefinition setRows(List<String> rows) {
        this.rows = rows;
        return this;
    }

    public String getVersion2() {
        return version2;
    }

    public void setVersion2(String version2) {
        this.version2 = version2;
    }

    @Override
    public void validate1() {
        validateNameAndLanguage();

        if (table == null) {
            table = getName();
        }

        if (StringUtils.isBlank(getComment())) {
            setComment(table);
        }

        //支持分表
        tables.addAll(Arrays.asList(table.split("[,，]", -1)));

        for (String t : tables) {
            ConfigDefinition other = parser.getTableConfigs().get(t);
            if (other != null && !getName().equals(other.getName())) {
                addValidatedError(getValidatedName() + other.getValidatedName() + "和表格[" + t + "]不能多对一");
            }
            parser.getTableConfigs().put(t, this);
        }

        parseValidationExpressions();

        for (FieldDefinition field : selfFields) {
            validateField(field);
        }
    }

    private void parseValidationExpressions() {
        List<Object> expressions = new ArrayList<>();

        for (Object validation : validations) {
            try {
                expressions.add(Ognl.parseExpression((String) validation));
            } catch (OgnlException e) {
                addValidatedError(getValidatedName() + "的校验规则[" + validation + "]错误:" + e.getMessage());
            }
        }

        validations.clear();
        validations.addAll(expressions);
    }

    @Override
    protected Set<String> getIllegalNames() {
        return illegalNames;
    }

    @Override
    public void validate2() {
        super.validate2();

        for (FieldDefinition field : fields) {
            if (field.getColumn() != null) {
                if (!columnFields.containsKey(field.getColumn())) {
                    columnFields.put(field.getColumn(), field);
                } else {
                    addValidatedError(getValidatedName("的") + field.getValidatedName() + "和列[" + field.getColumn() + "]必须一一对应");
                }
            }
        }

        IndexDefinition.validate(indexes, selfIndexes, selfFields);
    }

    @Override
    public void validate3() {
        validateDependents();
        for (FieldDefinition field : selfFields) {
            validateFieldRef(field);
            validateFieldRefLanguage(field);
            validateFieldDelimiter(field);
        }
    }

    @Override
    protected void validateParent() {
        super.validateParent();

        ConfigDefinition parent = getParent();
        if (parent == null) {
            return;
        }

        if (!parent.languages.containsAll(languages)) {
            addValidatedError(getValidatedName() + "支持的语言范围" + languages + "必须小于或等于其父配置[" + parentName + "]所支持的语言范围" + parent.languages);
        }

        ConfigDefinition ancestor = parent;
        Set<String> ancestors = new HashSet<>();

        while (ancestor != null) {
            if (ancestors.contains(ancestor.getLongName())) {
                return;
            }

            for (int i = ancestor.selfIndexes.size() - 1; i >= 0; i--) {
                IndexDefinition parentIndex = ancestor.selfIndexes.get(i).clone();
                parentIndex.setOwnerDefinition(this);
                indexes.add(0, parentIndex);
            }

            ancestors.add(ancestor.getLongName());
            ancestor = ancestor.getParent();
        }
    }


    @Override
    protected void validateField(FieldDefinition field) {
        validateFieldName(field);
        validateFieldType(field);
        validateFieldRange(field);
        validateFieldBeanCycle(field);

        if (field.getColumn() == null) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "对应的列不能为空");
        } else if (StringUtils.isBlank(field.getComment())) {
            field.setComment(field.getColumn());
        }

        if ("id".equals(field.getName()) && field.getIndex() == null) {
            field.setIndex("unique");
        }
    }

    /**
     * 校验字段的分隔符
     */
    private void validateFieldDelimiter(FieldDefinition field) {
        if (field.isCycle() || !field.isCollectionType()) {
            return;
        }

        ArrayList<String> delimiters = new ArrayList<>();
        validateFieldDelimiter(field, delimiters);

        if (delimiters.size() != new HashSet<>(delimiters).size()) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "关联分隔符有重复[" + String.join("", delimiters) + "]");
        }
    }

    /**
     * 转义分隔符里的正则表达式特殊字符
     */
    public static String escapeDelimiter(String delimiter) {
        if (delimiter == null || delimiter.length() != 1) {
            throw new IllegalArgumentException(delimiter);
        }
        if (Constants.NEED_ESCAPE_DELIMITERS.contains(delimiter)) {
            delimiter = "\\" + delimiter;
        }

        //兼容中文分隔符
        if (delimiter.contains(";")) {
            delimiter += "|" + "；";
        } else if (delimiter.contains(":")) {
            delimiter += "|" + "：";
        } else if (delimiter.contains("?")) {
            delimiter += "|" + "？";
        }

        return delimiter;
    }

    /**
     * 转换字段引用，因为相同配置类下的引用可以省略类名
     */
    @Override
    public String resolveFieldRef(String fieldRef) {
        if (!fieldRef.contains(".") && nameFields.containsKey(fieldRef)) {
            fieldRef = getName() + "." + fieldRef;
        }
        return fieldRef;
    }

}
