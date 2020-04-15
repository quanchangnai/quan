package quan.definition.data;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import quan.definition.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 数据定义
 * Created by quanchangnai on 2019/6/22.
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDefinitionType() {
        return 5;
    }

    @Override
    public String getDefinitionTypeName() {
        return "数据";
    }


    @Override
    public void setName(String name) {
        super.setName(name);
        underscoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, getName());
    }

    @Override
    public Pattern namePattern() {
        return Constants.DATA_NAME_PATTERN;
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
        indexes.add(indexDefinition);
    }

    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    @Override
    public void validate() {
        super.validate();
        if (getIdName() == null) {
            addValidatedError(getValidatedName() + "的主键不能为空");
            return;
        }
        if (getFields().stream().noneMatch(t -> t.getName().equals(getIdName()))) {
            addValidatedError(getValidatedName() + "的主键[" + getIdName() + "]不存在");
        }

        IndexDefinition.validateIndex(this, indexes, indexes, fields);
    }


}
