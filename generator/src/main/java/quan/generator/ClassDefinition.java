package quan.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    private String packageName;

    /**
     * 类定义所在的文件
     */
    private String definitionFile;

    /**
     * 类定义文本
     */
    private String definitionText;

    private List<FieldDefinition> fields = new ArrayList<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public String getFullName() {
        return packageName + "." + getName();
    }

    public String getDefinitionFile() {
        return definitionFile;
    }

    public ClassDefinition setDefinitionFile(String definitionFile) {
        this.definitionFile = definitionFile;
        return this;
    }

    public String getDefinitionText() {
        return definitionText;
    }

    public ClassDefinition setDefinitionText(String definitionText) {
        this.definitionText = definitionText;
        return this;
    }

    public void validate() {
        if (getName() == null || getName().trim().equals("")) {
            throwValidateError("类名不能为空");
        }

        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
    }

    protected void validateField(FieldDefinition fieldDefinition) {
        //校验字段名
        if (fieldDefinition.getName() == null || fieldDefinition.getName().trim().equals("")) {
            throwValidateError("字段名不能为空");
        }
    }

    protected void throwValidateError(String err) {
        throwValidateError(err, null);
    }

    protected void throwValidateError(String err, ClassDefinition other) {
        String errorPosition = ",文件:" + getDefinitionFile();
        if (getName() != null && !getName().trim().equals("")) {
            errorPosition += ",类:" + getName();
        }
        errorPosition += "\r\n\t" + definitionText;

        if (other != null) {
            errorPosition = ",文件:" + other.getDefinitionFile();
            if (getName() != null && !getName().trim().equals("")) {
                errorPosition += ",类:" + other.getName();
            }
            errorPosition += "\r\n\t" + definitionText;
        }

        throw new RuntimeException(err + errorPosition);
    }


    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name=" + getName() +
                ",packageName=" + getPackageName() +
                ",fields=" + getFields() +
                '}';
    }
}
