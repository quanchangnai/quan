package quan.definition;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 类定义
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    //不含前缀的默认包名，
    private String packageName;

    //不同语言对应的不含前缀的包名，为空时使用默认包名
    private Map<String, String> packageNames = new HashMap<>();

    //定义文件
    private String definitionFile;

    //定义文本
    private String definitionText;

    //是支持还是排除语言
    protected boolean excludeLanguage;

    //支持或者排除的语言
    protected Set<String> languages = new HashSet<>();

    //支持的语言
    protected Set<String> supportedLanguages;

    //导包，和具体英语相关
    private Set<String> imports = new HashSet<>();

    protected List<FieldDefinition> fields = new ArrayList<>();

    //字段名:字段定义
    protected Map<String, FieldDefinition> nameFields = new HashMap<>();


    @Override
    public String getDefinitionTypeName() {
        return "类";
    }

    @Override
    protected Pattern namePattern() {
        return Constants.CLASS_NAME_PATTERN;
    }

    public void reset() {
        imports.clear();
    }

    public String getPackagePrefix() {
        if (this instanceof EnumDefinition) {
            return StringUtils.isBlank(parser.getEnumPackagePrefix()) ? parser.getPackagePrefix() : parser.getEnumPackagePrefix();
        } else {
            return parser.getPackagePrefix();
        }
    }

    public String getNameWithPackage() {
        return packageName + "." + getName();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Map<String, String> getPackageNames() {
        return packageNames;
    }

    public String getPackageName(String lang) {
        if (!packageNames.containsKey(lang)) {
            return packageName;
        }
        return packageNames.get(lang);
    }

    public String getPackageName(Language lang) {
        return getPackageName(lang.name());
    }


    public String getFullPackageName(String lang) {
        String packagePrefix = getPackagePrefix();
        if (packagePrefix != null) {
            return packagePrefix + "." + getPackageName(lang);
        }
        return getPackageName(lang);
    }

    public String getFullPackageName(Language lang) {
        return getFullPackageName(lang.name());
    }


    public String getFullName(String lang) {
        return getFullPackageName(lang) + "." + getName();
    }

    public String getFullName(Language lang) {
        return getFullPackageName(lang) + "." + getName();
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
        if (StringUtils.isBlank(language) || category == DefinitionCategory.data) {
            return;
        }
        Pair<Boolean, Set<String>> pair = Language.parse(language);
        excludeLanguage = pair.getLeft();
        languages = pair.getRight();
    }

    public Set<String> getImports() {
        return imports;
    }

    public boolean supportLanguage(Language language) {
        boolean support = languages.isEmpty() || languages.contains(language.name());
        if (excludeLanguage) {
            support = !support;
        }
        return support;
    }


    public boolean supportLanguage(String language) {
        return supportLanguage(Language.valueOf(language));
    }

    public Set<String> getSupportedLanguages() {
        if (supportedLanguages != null) {
            return supportedLanguages;
        }
        supportedLanguages = new HashSet<>();
        for (Language language : Language.values()) {
            if (supportLanguage(language)) {
                supportedLanguages.add(language.name());
            }
        }
        return supportedLanguages;
    }

    public void validate() {
        validateNameAndLanguage();

        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
    }

    protected void validateNameAndLanguage() {
        if (getName() == null) {
            addValidatedError(getDefinitionTypeName() + "名不能为空");
        } else if (!namePattern().matcher(getName()).matches()) {
            addValidatedError(getDefinitionTypeName() + "名[" + getName() + "]格式错误,正确格式:" + namePattern());
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            addValidatedError(getName4Validate() + "的语言类型" + languages + "非法,合法的语言类型" + Language.names());
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
        if (!fieldDefinition.namePattern().matcher(fieldDefinition.getName()).matches()) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]格式错误,正确格式:" + fieldDefinition.namePattern());
            return;
        }

        if (isReservedWord(fieldDefinition.getName())) {
            addValidatedError(getName4Validate("的") + "字段名[" + fieldDefinition.getName() + "]不合法，不能使用保留字");
        }
    }

    protected boolean isReservedWord(String fieldName) {
        if (category == DefinitionCategory.data) {
            return Constants.JAVA_RESERVED_WORDS.contains(fieldName);
        }
        if (Constants.JAVA_RESERVED_WORDS.contains(fieldName)) {
            return true;
        }
        if (Constants.CS_RESERVED_WORDS.contains(fieldName)) {
            return true;
        }
        return Constants.LUA_RESERVED_WORDS.contains(fieldName);
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
            if (other.getName() != null) {
                position += "，类[" + other.getName() + "]";
            }
        }

        error += position + "。";
        parser.addValidatedError(error);
    }

}
