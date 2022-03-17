package quan.definition.config;

import org.apache.commons.lang3.StringUtils;
import quan.definition.*;
import quan.util.CommonUtils;

import java.io.File;
import java.util.*;

/**
 * 配置定义
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends BeanDefinition {

    //配置本身对应的表，有分表以逗号分格
    private String table;

    //配置对应的表，包含分表
    private TreeSet<String> tables = new TreeSet<>();

    //配置对应的表，包含分表和子表
    private TreeSet<String> allTables = new TreeSet<>();

    //所有索引,包含继承下来的索引
    private List<IndexDefinition> indexes = new LinkedList<>();

    //仅自己的索引
    private List<IndexDefinition> selfIndexes = new LinkedList<>();

    //列名:字段
    private Map<String, FieldDefinition> columnFields = new HashMap<>();

    private List<String> rows = new ArrayList<>();

    private Set<ConstantDefinition> constantDefinitions = new HashSet<>();

    {
        category = Category.config;
    }

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
        return 6;
    }

    @Override
    public String getKindName() {
        return "配置";
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (table == null) {
            table = getName();
        }
    }

    @Override
    public ConfigDefinition getParent() {
        return parser.getConfig(getWholeParentName());
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

        for (String configName : getMeAndDescendantLongNames()) {
            ConfigDefinition configDefinition = parser.getConfig(configName);
            if (configDefinition == null) {
                continue;
            }
            allTables.addAll(Arrays.asList(configDefinition.table.split(",", -1)));
        }

        return allTables;
    }


    @Override
    public void addField(FieldDefinition fieldDefinition) {
        super.addField(fieldDefinition);
        if (fieldDefinition.getName().equals("id") && fieldDefinition.getIndex() == null) {
            fieldDefinition.setIndex("unique");
        }
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public List<IndexDefinition> getSelfIndexes() {
        return selfIndexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexDefinition.setOwner(this);
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

    public List<String> getRows() {
        return rows;
    }

    public ConfigDefinition setRows(List<String> rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public void validate1() {
        validateNameAndLanguage();

        if (table == null) {
            table = getName();
        }

        //支持分表
        tables.addAll(Arrays.asList(table.split(",", -1)));
        for (String t : tables) {
            ConfigDefinition other = parser.getTableConfigs().get(t);
            if (other != null && !getName().equals(other.getName())) {
                addValidatedError(getValidatedName() + other.getValidatedName() + "和表格[" + t + "]不能多对一");
            }
            parser.getTableConfigs().put(t, this);
        }

        if (StringUtils.isBlank(getComment())) {
            StringBuilder comment = new StringBuilder();
            boolean start = true;
            for (String t1 : tables) {
                if (!start) {
                    comment.append(",");
                }
                start = false;
                String t2 = CommonUtils.toPlatPath(t1);
                int lastSepIndex = t2.lastIndexOf(File.separator);
                if (lastSepIndex >= 0) {
                    comment.append(t2.substring(lastSepIndex + 1));
                } else {
                    comment.append(t2);
                }
            }
            setComment(comment.toString());
        }

        for (FieldDefinition field : selfFields) {
            validateField(field);
        }
    }

    @Override
    public void validate2() {
        super.validate2();

        for (FieldDefinition field : fields) {
            validateFieldNameDuplicate(field);

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
        super.validate3();
        for (FieldDefinition field : selfFields) {
            //校验字段引用
            validateFieldRef(field);
            //校验字段支持的语言
            validateFieldLanguage(field);
        }
    }

    @Override
    protected void validateParent() {
        super.validateParent();

        ConfigDefinition parent = getParent();
        if (parent == null) {
            return;
        }

        if (!parent.getSupportedLanguages().containsAll(getSupportedLanguages())) {
            addValidatedError(getValidatedName() + "支持的语言范围" + supportedLanguages + "必须小于或等于其父配置[" + parentName + "]所支持的语言范围" + parent.supportedLanguages);
        }

        Set<String> ancestors = new HashSet<>();
        while (parent != null) {
            if (ancestors.contains(parent.getName())) {
                return;
            }
            ancestors.add(parent.getName());

            for (int i = parent.selfIndexes.size() - 1; i >= 0; i--) {
                IndexDefinition parentIndex = parent.selfIndexes.get(i).clone();
                parentIndex.setOwner(this);
                indexes.add(0, parentIndex);
            }

            parent = parent.getParent();
        }
    }


    @Override
    protected void validateField(FieldDefinition field) {
        //校验字段名
        validateFieldName(field);

        //校验字段类型
        validateFieldType(field);

        //校验字段循环依赖
        validateFieldBeanCycle(field);

        //校验字段依赖语言
        validateFieldBeanLanguage(field);

        if (field.getColumn() == null) {
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "对应的列不能为空");
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
            addValidatedError(getValidatedName("的索引") + field.getValidatedName() + "不支持设置语言");
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
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "支持的语言类型" + fieldIllegalLanguages + "非法,合法语言类型" + supportedLanguages);
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
            addValidatedError(getValidatedName("的") + field.getValidatedName() + "关联分隔符有重复[" + String.join("", delimiterList) + "]");
        }
    }

    private void validateFieldDelimiter(FieldDefinition field, List<String> delimiters) {
        if (field.isBeanType()) {
            validateBeanDelimiter(field.getTypeBean(), delimiters);
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
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "的分隔符[" + delimiter + "]非法,合法分隔符" + Constants.LEGAL_DELIMITERS);
            }
        }

        int charNumError = 0;
        if (delimiter.length() != 1 && (field.isBeanType() || field.isListType() || field.isSetType())) {
            charNumError = 1;
        }
        if (field.isMapType()) {
            if (delimiter.length() != 2) {
                charNumError = 2;
            } else if (delimiter.charAt(0) == delimiter.charAt(1)) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[map]的分隔符[" + delimiter + "]必须是2个不相同的字符");
            }
        }
        if (charNumError > 0) {
            addValidatedError(getValidatedName() + "[" + field.getType() + "]类型字段" + field.getValidatedName() + "的分隔符[" + delimiter + "]必须是" + charNumError + "个字符");
        }

        BeanDefinition fieldValueBean = field.getValueTypeBean();
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
