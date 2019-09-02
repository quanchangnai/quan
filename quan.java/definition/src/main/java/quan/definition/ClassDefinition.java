package quan.definition;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 类定义
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    //包前缀
    private String packagePrefix;

    //包名
    private String packageName;

    //定义文件
    private String definitionFile;

    //定义文本
    private String definitionText;

    protected List<String> languages = new ArrayList<>();

    protected List<FieldDefinition> fields = new ArrayList<>();

    //字段名:字段定义
    protected Map<String, FieldDefinition> nameFields = new HashMap<>();

    //保留字段名
    protected Set<String> reservedFieldNames = new HashSet<>();

    @Override
    public String getDefinitionTypeName() {
        return "类";
    }

    @Override
    protected String namePattern() {
        return Constants.CLASS_NAME_PATTERN;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullPackageName() {
        if (packagePrefix != null) {
            return packagePrefix + "." + packageName;

        }
        return packageName;
    }

    public void setPackagePrefix(String packagePrefix) {
        if (StringUtils.isBlank(packagePrefix)) {
            return;
        }
        this.packagePrefix = packagePrefix.trim();
    }

    public void setPackageName(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return;
        }
        this.packageName = packageName.trim();
    }

    public String getFullName() {
        return getFullPackageName() + "." + getName();
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public Map<String, FieldDefinition> getNameFields() {
        return nameFields;
    }

    public void addField(FieldDefinition fieldDefinition) {
        fields.add(fieldDefinition);
    }

    public FieldDefinition getField(String fieldName) {
        if (nameFields.containsKey(fieldName)) {
            return nameFields.get(fieldName);
        }

        for (FieldDefinition field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        return null;
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

    public void setLang(String language) {
        if (StringUtils.isBlank(language)) {
            return;
        }
        for (String lang : language.trim().split(",", -1)) {
            this.languages.add(lang.trim());
        }
    }

    public boolean supportLanguage(Language language) {
        return languages.isEmpty() || languages.contains(language.name());
    }

    public void validate() {
        if (getName() == null) {
            addValidatedError(getDefinitionTypeName() + "名不能为空");
        } else if (!Pattern.matches(namePattern(), getName())) {
            addValidatedError(getDefinitionTypeName() + "名[" + getName() + "]格式错误");
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            addValidatedError(getName4Validate() + "的语言类型" + languages + "非法,合法的语言类型" + Language.names());
        }

        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
    }

    /**
     * 依赖validate()的结果，必须等所有类的validate()执行完成后再执行
     */
    public void validate2() {
    }

    protected void validateField(FieldDefinition fieldDefinition) {
        validateFieldNameSelf(fieldDefinition);
        validateFieldNameDuplicate(fieldDefinition);
    }

    protected void validateFieldNameSelf(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            addValidatedError(getName4Validate("的") + "字段名不能为空");
            return;
        }

        //校验字段名格式
        if (!Pattern.matches(fieldDefinition.namePattern(), fieldDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]格式错误");
            return;
        }

        if (!reservedFieldNames.isEmpty() && reservedFieldNames.contains(fieldDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]不合法，不能使用保留字段名" + reservedFieldNames);
        }
    }

    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            return;
        }
        if (nameFields.containsKey(fieldDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]不能重复");
        } else {
            nameFields.put(fieldDefinition.getName(), fieldDefinition);
        }
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
        parser.addValidatedError(error);
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
