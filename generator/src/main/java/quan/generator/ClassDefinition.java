package quan.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类定义
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    /**
     * 包名
     */
    private String packageName;

    /**
     * 类定义文件
     */
    private String definitionFile;

    /**
     * 类定义文本
     */
    private String definitionText;

    protected List<String> languages = new ArrayList<>();

    protected List<FieldDefinition> fields = new ArrayList<>();

    protected Map<String, FieldDefinition> fieldMap = new HashMap<>();

    private static Map<String, ClassDefinition> all = new HashMap<>();

    private static List<String> validatedErrors = new ArrayList<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }


    public void addField(FieldDefinition fieldDefinition) {
        fields.add(fieldDefinition);
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

    public static Map<String, ClassDefinition> getAll() {
        return all;
    }

    public void setLang(String language) {
        if (language == null) {
            return;
        }
        for (String lang : language.trim().split(",")) {
            this.languages.add(lang.trim());
        }
    }

    public boolean supportLanguage(Language language) {
        return languages.isEmpty() || languages.contains(language.name());
    }

    public void validate() {
        if (getName() == null) {
            addValidatedError("类名不能为空");
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            addValidatedError("类" + getName4Validate() + "的语言类型" + languages + "非法,支持的语言类型" + Language.names());
        }

        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
    }

    protected void validateField(FieldDefinition fieldDefinition) {
        //校验字段名
        if (fieldDefinition.getName() == null) {
            addValidatedError(getName4Validate("的") + "字段名不能为空");
            return;
        }
        if (fieldMap.containsKey(fieldDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]重复");
            return;
        }
        fieldMap.put(fieldDefinition.getName(), fieldDefinition);
    }
    
    protected void addValidatedError(String error) {
        addValidatedError(error, null);
    }

    protected void addValidatedError(String error, ClassDefinition other) {
        String position = "。定义文件[" + getDefinitionFile() + "]";
        if (getName() != null) {
            position += "，类[" + getName() + "]";
        }

        if (other != null) {
            position += "。定义文件[" + other.getDefinitionFile() + "]";
            if (getName() != null) {
                position += "，类[" + other.getName() + "]";
            }
        }

        error += position + "。";
        validatedErrors.add(error);
    }

    public static List<String> getValidatedErrors() {
        return validatedErrors;
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
