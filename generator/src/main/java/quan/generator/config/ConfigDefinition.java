package quan.generator.config;

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


    public String getTable() {
        return table;
    }

    public ConfigDefinition setTable(String table) {
        this.table = table;
        return this;
    }


    public String getParent() {
        if (isParentWithPackage()) {
            return parent.substring(parent.lastIndexOf(".") + 1);
        }
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

    public boolean isParentWithPackage() {
        if (parent == null) {
            return false;
        }
        return parent.contains(".");
    }

    public String getParentWithPackage() {
        if (!isParentWithPackage()) {
            return null;
        }
        return parent;
    }

    public String getParentPackage() {
        if (isParentWithPackage()) {
            return parent.substring(0, parent.lastIndexOf("."));
        }
        return null;
    }

    public ConfigDefinition setParent(String parent) {
        if (parent != null && !parent.trim().equals("")) {
            this.parent = parent;
        }
        return this;
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexes.add(indexDefinition);
        selfIndexes.add(indexDefinition);
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
        if (getName() == null || getName().trim().equals("")) {
            throwValidatedError("类名不能为空");
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            throwValidatedError("语言类型" + languages + "非法,支持的语言类型" + Language.names());
        }

        if (table == null || table.trim().equals("")) {
            throwValidatedError("配置[" + getName() + "]的对应表格不能为空");
        }

        //支持分表
        String[] tables = table.split(",");
        for (String t : tables) {
            ConfigDefinition other = tableConfigs.get(t);
            if (other != null) {
                throwValidatedError("配置[" + getName() + "," + other.getName() + "]和表格[" + t + "]不能多对一", other);
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
            throwValidatedError("配置[" + getName() + "]的父类[" + parent + "]不存在");
        }
        if (!(parentClassDefinition instanceof ConfigDefinition)) {
            throwValidatedError("配置[" + getName() + "]的父类[" + parent + "]也必须是配置类");
        }

        Set<String> parents = new HashSet<>();
        ConfigDefinition parentConfigDefinition = getParentDefinition();

        while (parentConfigDefinition != null) {
            if (parents.contains(parentConfigDefinition.getName())) {
                throwValidatedError("配置[" + getName() + "]的父类" + parents + "不能有循环");
            }
            parents.add(parentConfigDefinition.getName());

            fields.addAll(0, parentConfigDefinition.selfFields);
            indexes.addAll(0, parentConfigDefinition.selfIndexes);
            parentConfigDefinition.children.add(getName());

            parentConfigDefinition = parentConfigDefinition.getParentDefinition();
        }

    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        if (fieldDefinition.getColumn() == null || fieldDefinition.getColumn().trim().equals("")) {
            throwValidatedError("字段对应的列[" + fieldDefinition.getName() + "]不能为空");
        }
        if (columnFields.containsKey(fieldDefinition.getColumn())) {
            throwValidatedError("字段[" + fieldDefinition.getName() + "]和列[" + fieldDefinition.getColumn() + "]必须一一对应");
        }

        if (fieldDefinition.getComment() == null || fieldDefinition.getComment().trim().equals("")) {
            fieldDefinition.setComment(fieldDefinition.getColumn());
        }


        columnFields.put(fieldDefinition.getColumn(), fieldDefinition);
    }

    private void validateIndexes() {
        for (FieldDefinition selfField : selfFields) {
            if (selfField.getIndex() == null) {
                continue;
            }
            IndexDefinition indexDefinition = new IndexDefinition(this);
            indexDefinition.setName(selfField.getName());
            indexDefinition.setFieldNames(selfField.getName());
            indexDefinition.setType(selfField.getIndex());
            if (selfField.getComment() == null || selfField.getComment().trim().equals("")) {
                indexDefinition.setComment(selfField.getColumn());
            } else {
                indexDefinition.setComment(selfField.getComment());
            }
            addIndex(indexDefinition);
        }

        if (indexes.isEmpty()) {
            throwValidatedError("配置[" + getName() + "]至少需要一个索引");
        }
        for (IndexDefinition indexDefinition : selfIndexes) {
            validateIndex(indexDefinition);
        }
        Set<String> indexNames = new HashSet<>();
        for (IndexDefinition indexDefinition : indexes) {
            if (indexNames.contains(indexDefinition.getName())) {
                throwValidatedError("索引名[" + indexDefinition.getName() + "]重复");
            }
            indexNames.add(indexDefinition.getName());
        }
    }

    private void validateIndex(IndexDefinition indexDefinition) {
        if (indexDefinition.getName() == null || indexDefinition.getName().trim().equals("")) {
            throwValidatedError("索引名不能为空");
        }

        String indexType = indexDefinition.getType();
        if (indexType == null || indexType.trim().equals("")) {
            throwValidatedError("索引类型不能为空");
        }
        List<String> allowIndexTypes = Arrays.asList("normal", "n", "unique", "u");
        if (!allowIndexTypes.contains(indexType)) {
            throwValidatedError("索引类型[" + indexType + "]非法,允许类型" + allowIndexTypes);
        }

        String fieldNames = indexDefinition.getFieldNames();
        if (fieldNames == null || fieldNames.trim().equals("")) {
            throwValidatedError("索引[" + indexDefinition.getName() + "]的字段不能为空");
        } else {
            String[] fieldNameArray = fieldNames.split("[,]");
            if (fieldNameArray.length > 3) {
                throwValidatedError("索引[" + indexDefinition.getName() + "]的字段[" + fieldNames + "]不能超过三个");
            }
            for (String fieldName : fieldNameArray) {
                FieldDefinition fieldDefinition = fieldMap.get(fieldName);
                if (fieldDefinition == null) {
                    throwValidatedError("索引[" + indexDefinition.getName() + "]的字段[" + fieldName + "]不存在");
                }
                if (!indexDefinition.addField(fieldDefinition)) {
                    throwValidatedError("索引[" + indexDefinition.getName() + "]的字段[" + fieldNames + "]不能重复");
                }
            }
        }
    }
}
