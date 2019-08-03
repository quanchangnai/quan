package quan.generator.config;

import org.apache.commons.lang3.StringUtils;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;
import quan.generator.Language;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends BeanDefinition {

    //配置本身对应的表，有分表以逗号分格
    private String table;

    //配置对应的表，包含分表和子表
    private List<String> tables = new ArrayList<>();

    //配置的父类
    private String parent;

    //配置的所有子孙类
    private Set<String> children = new HashSet<>();

    private List<IndexDefinition> indexes = new ArrayList<>();

    private List<IndexDefinition> selfIndexes = new ArrayList<>();

    protected List<FieldDefinition> selfFields = new ArrayList<>();

    private Map<String, FieldDefinition> columnFields = new HashMap<>();

    private static Map<String, ConfigDefinition> tableConfigs = new HashMap<>();

    public static Set<String> allowDelimiters = new HashSet<>(Arrays.asList(";", "_", "*", "|", "$", "@", "#", "&", "?"));

    public static Set<String> needEscapeChars = new HashSet<>(Arrays.asList("*", "|", "?"));

    public ConfigDefinition() {
    }

    public ConfigDefinition(String table, String parent) {
        this.table = table;
        this.parent = parent;
    }

    @Override
    public int getDefinitionType() {
        return 6;
    }

    @Override
    public String getDefinitionTypeName() {
        return "配置";
    }

    public String getTable() {
        return table;
    }

    public ConfigDefinition setTable(String table) {
        if (StringUtils.isBlank(table)) {
            return this;
        }
        this.table = table.trim();
        return this;
    }


    public String getParent() {
        return parent;
    }

    public ConfigDefinition getParentDefinition() {
        ClassDefinition classDefinition = ClassDefinition.getAll().get(getParent());
        if (classDefinition instanceof ConfigDefinition) {
            return (ConfigDefinition) classDefinition;
        }
        return null;
    }

    public List<String> getTables() {
        if (!tables.isEmpty()) {
            return tables;
        }

        Set<String> childrenAndMe = new HashSet<>(children);
        childrenAndMe.add(getName());

        for (String configName : childrenAndMe) {
            ConfigDefinition configDefinition = (ConfigDefinition) getAll().get(configName);
            tables.addAll(Arrays.asList(configDefinition.table.split("[,]")));
        }

        return tables;
    }


    public ConfigDefinition setParent(String parent) {
        if (StringUtils.isBlank(parent)) {
            return this;
        }
        this.parent = parent.trim();
        return this;
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexes.add(indexDefinition);
        selfIndexes.add(indexDefinition);
    }

    public IndexDefinition getIndexByStartField(FieldDefinition field) {
        for (IndexDefinition index : indexes) {
            if (index.getFields().get(0) == field) {
                return index;
            }
        }
        return null;
    }

    public Map<String, FieldDefinition> getColumnFields() {
        return columnFields;
    }

    public static Map<String, ConfigDefinition> getTableConfigs() {
        return tableConfigs;
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
            addValidatedError(getName4Validate() + "的语言类型" + languages + "非法,支持的语言类型" + Language.names());
        }

        if (table == null) {
            addValidatedError(getName4Validate() + "对应的表格不能为空");
        }

        //支持分表
        String[] tables = table.split(",");
        for (String t : tables) {
            ConfigDefinition other = tableConfigs.get(t);
            if (other != null) {
                addValidatedError(getName4Validate() + other.getName4Validate() + "和表格[" + t + "]不能多对一", other);
            }
            tableConfigs.put(t, this);
        }

        validateParent();

        for (FieldDefinition fieldDefinition : fields) {
            validateField(fieldDefinition);
        }

        validateIndexes();
    }

    private void validateParent() {
        if (getParent() == null) {
            return;
        }

        //支持子表
        ClassDefinition parentClassDefinition = ClassDefinition.getAll().get(getParent());
        if (parentClassDefinition == null) {
            addValidatedError(getName4Validate() + "的父类[" + parent + "]不存在");
            return;
        }
        if (!(parentClassDefinition instanceof ConfigDefinition)) {
            addValidatedError(getName4Validate() + "的父类[" + parent + "]也必须是配置类");
            return;
        }

        ConfigDefinition parentConfigDefinition = getParentDefinition();

        Set<String> parents = new HashSet<>();
        while (parentConfigDefinition != null) {
            if (parents.contains(parentConfigDefinition.getName())) {
                addValidatedError(getName4Validate() + "和父类" + parents + "不能有循环");
                return;
            }
            parents.add(parentConfigDefinition.getName());
            parentConfigDefinition = parentConfigDefinition.getParentDefinition();
        }

        parentConfigDefinition = getParentDefinition();
        while (parentConfigDefinition != null) {
            fields.addAll(0, parentConfigDefinition.selfFields);
            indexes.addAll(0, parentConfigDefinition.selfIndexes);
            parentConfigDefinition.children.add(getName());
            parentConfigDefinition = parentConfigDefinition.getParentDefinition();
        }

    }


    @Override
    protected void validateField(FieldDefinition field) {
        super.validateField(field);

        if (field.getColumn() == null) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "对应的列不能为空");
            return;
        }
        if (columnFields.containsKey(field.getColumn())) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "和列[" + field.getColumn() + "]必须一一对应");
        } else {
            columnFields.put(field.getColumn(), field);
        }

        if (field.getComment() == null) {
            field.setComment(field.getColumn());
        }

        //校验字段的分隔符
        ArrayList<String> delimiterList = new ArrayList<>();
        validateDelimiter(field, delimiterList);
        Set<String> delimiterSet = new HashSet<>(delimiterList);
        if (delimiterList.size() != delimiterSet.size()) {
            addValidatedError(getName4Validate("的") + field.getName4Validate() + "的分隔符有重复[" + String.join("", delimiterList) + "]");
        }

    }

    private void validateDelimiter(FieldDefinition field, List<String> delimiters) {
        if (field.isBeanType()) {
            validateDelimiter(field.getBean(), delimiters);
            return;
        }

        if (!field.isCollectionType()) {
            return;
        }

        String delimiter = field.getDelimiter();

        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            delimiters.add(s);
            if (!allowDelimiters.contains(s)) {
                addValidatedError(getName4Validate("的") + field.getName4Validate() + "的分隔符[" + delimiter + "]非法,合法分隔符" + allowDelimiters);
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
                addValidatedError(getName4Validate() + "map类型字段" + field.getName4Validate() + "的分隔符[" + delimiter + "]必须是2个不相同的字符");
            }
        }
        if (charNumError > 0) {
            addValidatedError(getName4Validate() + "[" + field.getType() + "]类型字段" + field.getName4Validate() + "的分隔符[" + delimiter + "]必须是" + charNumError + "个字符");
        }

        if (field.isBeanValueType()) {
            BeanDefinition fieldValueBean = (BeanDefinition) ClassDefinition.getAll().get(field.getValueType());
            validateDelimiter(fieldValueBean, delimiters);
        }
    }

    private void validateDelimiter(BeanDefinition beanDefinition, List<String> delimiters) {
        String delimiter = beanDefinition.getDelimiter();
        for (int i = 0; i < delimiter.length(); i++) {
            delimiters.add(String.valueOf(delimiter.charAt(i)));
        }
        for (FieldDefinition beanField : beanDefinition.getFields()) {
            validateDelimiter(beanField, delimiters);
        }
    }

    @Override
    public void validate2() {
        for (FieldDefinition selfField : selfFields) {
            validateRef(selfField);
        }

    }

    protected boolean supportRef() {
        return true;
    }

    private void validateIndexes() {
        for (FieldDefinition selfField : selfFields) {
            if (selfField.getIndex() == null && !selfField.getName().equals("id")) {
                continue;
            }
            if (!selfField.isPrimitiveType() && !selfField.isEnumType()) {
                addValidatedError(getName4Validate("的") + selfField.getName4Validate() + "类型[" + selfField.getType() + "]不支持索引，允许的类型为" + FieldDefinition.primitiveTypes + "或枚举");
                continue;
            }
            IndexDefinition indexDefinition = new IndexDefinition(this);
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
        }

        String indexType = indexDefinition.getType();
        if (indexType == null) {
            addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "类型不能为空");
        } else {
            List<String> allowIndexTypes = Arrays.asList("normal", "n", "unique", "u");
            if (!allowIndexTypes.contains(indexType)) {
                addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "类型[" + indexType + "]非法,允许类型" + allowIndexTypes);
            }
        }

        String fieldNames = indexDefinition.getFieldNames();
        if (fieldNames == null) {
            addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "的字段不能为空");
            return;
        }

        String[] fieldNameArray = fieldNames.split("[,]");
        if (fieldNameArray.length > 3) {
            addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "的字段[" + fieldNames + "]不能超过三个");
        }
        for (String fieldName : fieldNameArray) {
            FieldDefinition fieldDefinition = fieldMap.get(fieldName);
            if (fieldDefinition == null) {
                addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "的字段[" + fieldName + "]不存在");
                continue;
            }
            if (!fieldDefinition.isPrimitiveType() && !fieldDefinition.isEnumType()) {
                addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "的字段[" + fieldName + "]类型[" + fieldDefinition.getType() + "]非法，允许的类型为" + FieldDefinition.primitiveTypes + "或枚举");
            }
            if (!indexDefinition.addField(fieldDefinition)) {
                addValidatedError(getName4Validate() + "的索引" + indexDefinition.getName4Validate() + "的字段[" + fieldNames + "]不能重复");
            }
        }

    }


    public static String escapeDelimiter(String delimiter) {
        StringBuilder escapedDelimiter = new StringBuilder();
        for (int i = 0; i < delimiter.length(); i++) {
            String s = String.valueOf(delimiter.charAt(i));
            if (i > 0) {
                escapedDelimiter.append("|");
            }
            if (needEscapeChars.contains(s)) {
                escapedDelimiter.append("\\");
            }
            escapedDelimiter.append(s);
        }
        return escapedDelimiter.toString();
    }

}
