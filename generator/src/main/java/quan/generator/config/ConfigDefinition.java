package quan.generator.config;

import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends BeanDefinition {

    private String source;

    private String parent;

    private List<IndexDefinition> indexes = new ArrayList<>();

    private Map<String, FieldDefinition> sourceFields = new HashMap<>();

    private static Map<String, ConfigDefinition> sourceConfigs = new HashMap<>();

    public ConfigDefinition() {
    }

    public ConfigDefinition(String source, String parent) {
        this.source = source;
        this.parent = parent;
    }

    @Override
    public int getDefinitionType() {
        return 6;
    }


    public String getSource() {
        return source;
    }

    public ConfigDefinition setSource(String source) {
        this.source = source;
        return this;
    }


    public String getParent() {
        if (isParentWithPackage()) {
            return parent.substring(parent.lastIndexOf(".") + 1);
        }
        return parent;
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
    }

    public Map<String, FieldDefinition> getSourceFields() {
        return sourceFields;
    }

    public static Map<String, ConfigDefinition> getSourceConfigs() {
        return sourceConfigs;
    }

    @Override
    public void addField(FieldDefinition fieldDefinition) {
        super.addField(fieldDefinition);

        sourceFields.put(fieldDefinition.getSource(), fieldDefinition);

        if (fieldDefinition.getIndex() != null) {
            IndexDefinition indexDefinition = new IndexDefinition(this);
            indexDefinition.setName(fieldDefinition.getName());
            indexDefinition.setFieldNames(fieldDefinition.getName());
            indexDefinition.setType(fieldDefinition.getIndex());
            if (fieldDefinition.getComment() == null || fieldDefinition.getComment().trim().equals("")) {
                indexDefinition.setComment(fieldDefinition.getSource());
            } else {
                indexDefinition.setComment(fieldDefinition.getComment());
            }
            addIndex(indexDefinition);
        }

    }

    @Override
    public void validate() {
        super.validate();

        if (source == null || source.trim().equals("")) {
            throwValidatedError("配置[" + getName() + "]的来源不能为空");
        }

        ConfigDefinition other = sourceConfigs.get(source);
        if (other != null) {
            throwValidatedError("配置的来源[" + source + "]不能重复", other);
        }
        sourceConfigs.put(source, this);

        if (getParent() != null) {
            ClassDefinition parentClassDefinition = ClassDefinition.getAll().get(getParent());
            if (parentClassDefinition == null) {
                throwValidatedError("配置[" + getName() + "]的父类[" + parent + "]不存在");
            }
            if (!(parentClassDefinition instanceof ConfigDefinition)) {
                throwValidatedError("配置[" + getName() + "]的父类[" + parent + "]不合法，父类也必须是配置类");
            }

        }

        if (indexes.isEmpty()) {
            throwValidatedError("配置[" + getName() + "]至少需要一个索引");
        }
        Set<String> indexNames = new HashSet<>();
        for (IndexDefinition indexDefinition : indexes) {
            validateIndex(indexDefinition);
            if (indexNames.contains(indexDefinition.getName())) {
                throwValidatedError("索引名[" + indexDefinition.getName() + "]重复");
            }
            indexNames.add(indexDefinition.getName());
        }

    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        if (fieldDefinition.getSource() == null || fieldDefinition.getSource().trim().equals("")) {
            throwValidatedError("字段[" + fieldDefinition.getName() + "]的来源不能为空");
        }

        if (fieldDefinition.getComment() == null || fieldDefinition.getComment().trim().equals("")) {
            fieldDefinition.setComment(fieldDefinition.getSource());
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
        }

        String[] fieldNameArray = fieldNames.split(",");
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
