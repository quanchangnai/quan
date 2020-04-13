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

    //ID字段的类型
    private String idType;

    //ID字段的名字
    private String idName = "id";

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
    public void setName(String name) {
        super.setName(name);
        underscoreName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, getName());
    }

    @Override
    public Pattern namePattern() {
        return Constants.DATA_NAME_PATTERN;
    }

    public String getIdType() {
        return idType;
    }

    public DataDefinition setIdType(String idType) {
        this.idType = idType;
        return this;
    }

    public String getIdName() {
        return idName;
    }

    @Override
    public int getDefinitionType() {
        return 5;
    }

    @Override
    public String getDefinitionTypeName() {
        return "数据";
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

        validateIndexes();
    }


    protected void validateIndexes() {
        for (FieldDefinition field : fields) {
            if (!IndexDefinition.isIndex(field.getIndex())) {
                continue;
            }

            if (!field.isPrimitiveType() && !field.isEnumType() && field.getType() != null) {
                addValidatedError(getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]不支持索引，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
                continue;
            }

            indexes.add(new IndexDefinition(field));
        }

        indexes.forEach(index -> index.validateIndex(this, false));

    }


}
