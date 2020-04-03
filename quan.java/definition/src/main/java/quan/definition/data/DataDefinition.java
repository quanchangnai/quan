package quan.definition.data;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.Constants;

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
    protected Pattern namePattern() {
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
    }

}
