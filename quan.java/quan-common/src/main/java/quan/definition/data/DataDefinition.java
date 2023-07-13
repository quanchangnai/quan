package quan.definition.data;

import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.FieldDefinition;
import quan.definition.IndexDefinition;
import quan.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 数据定义
 */
public class DataDefinition extends BeanDefinition {

    //ID字段的名字
    private String idName = "id";

    //ID字段
    private FieldDefinition idField;

    private List<IndexDefinition> indexes = new ArrayList<>();

    {
        category = Category.data;
    }

    public DataDefinition() {
    }

    public DataDefinition(String idName) {
        if (!StringUtils.isBlank(idName)) {
            this.idName = idName;
        }
    }

    @Override
    public DataDefinition setCategory(Category category) {
        if (category != this.category) {
            throw new IllegalStateException();
        }
        return this;
    }

    @Override
    public int getKind() {
        return KIND_DATA;
    }

    @Override
    public String getKindName() {
        return "数据";
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (name != null) {
            underscoreName = StringUtils.toSnakeCase(name, true);
        }
    }

    @Override
    public boolean isAllowSameName() {
        return false;
    }

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getDataNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
    }

    public String getIdName() {
        return idName;
    }

    public FieldDefinition getIdField() {
        return idField;
    }

    public DataDefinition setIdField(FieldDefinition idField) {
        this.idField = idField;
        return this;
    }

    public void addIndex(IndexDefinition indexDefinition) {
        indexDefinition.setOwnerDefinition(this);
        indexes.add(indexDefinition);
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    @Override
    public void validate2() {
        super.validate2();

        if (getIdName() == null) {
            addValidatedError(getValidatedName() + "的主键不能为空");
        } else {
            idField = nameFields.get(getIdName());
            if (idField == null) {
                addValidatedError(getValidatedName() + "的主键[" + getIdName() + "]不存在对应字段");
            } else if (!idField.isPrimitiveType()) {
                addValidatedError(getValidatedName() + "的主键[" + getIdName() + "]类型[" + idField.getType() + "]不合法");
            }
        }

        IndexDefinition.validate(indexes, indexes, fields);

        boolean textIndex = false;
        for (IndexDefinition indexDefinition : indexes) {
            if (indexDefinition.isText()) {
                if (textIndex) {
                    addValidatedError(getValidatedName() + "最多只能定义一个文本(组合)索引");
                } else {
                    textIndex = true;
                }
            }
        }
    }

}
