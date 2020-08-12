package quan.definition;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import quan.definition.DependentSource.DependentType;

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

    //依赖的类，Map<依赖的类名, <来源, ClassDefinition>>
    protected Map<String, TreeMap<DependentSource, ClassDefinition>> dependentsClasses = new HashMap<>();

    protected List<FieldDefinition> fields = new ArrayList<>();

    //导包，和具体语言相关
    private Map<String, String> imports = new TreeMap<>();

    //字段名:字段定义
    protected Map<String, FieldDefinition> nameFields = new HashMap<>();


    @Override
    public String getKindName() {
        return "类";
    }

    @Override
    public Pattern getNamePattern() {
        return Constants.CLASS_NAME_PATTERN;
    }

    public void reset() {
        imports.clear();
        fields.forEach(this::resetField);
    }

    protected void resetField(FieldDefinition fieldDefinition) {
        fieldDefinition.setBasicType(null);
        fieldDefinition.setClassType(null);
        fieldDefinition.setBasicKeyType(null);
        fieldDefinition.setClassKeyType(null);
        fieldDefinition.setBasicValueType(null);
        fieldDefinition.setClassValueType(null);
    }

    public String getPackagePrefix() {
        if (this instanceof EnumDefinition) {
            return StringUtils.isBlank(parser.getEnumPackagePrefix()) ? parser.getPackagePrefix() : parser.getEnumPackagePrefix();
        } else {
            return parser.getPackagePrefix();
        }
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


    public String getLongName() {
        return getLongClassName(this, getName());
    }


    /**
     * 不带包名的类名
     */
    public static String getShortClassName(String className) {
        if (className == null) {
            return null;
        }
        int index = className.indexOf(".");
        if (index >= 0) {
            return className.substring(index + 1);
        }
        return className;
    }

    /**
     * 和具体语言环境无关的[不含前缀的包名.类名]
     */
    public static String getLongClassName(ClassDefinition owner, String className) {
        if (!StringUtils.isBlank(className) && !className.contains(".")) {
            className = owner.getPackageName() + "." + className;
        }
        return className;
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

    //其他类使用本类时的导入包
    public String getOtherImport(Language language) {
        if (language == Language.cs) {
            return getFullPackageName(language);
        }
        return getFullName(language);
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public Map<String, FieldDefinition> getNameFields() {
        return nameFields;
    }

    public void addField(FieldDefinition fieldDefinition) {
        fieldDefinition.setOwner(this);
        fields.add(fieldDefinition);
    }

    public FieldDefinition getField(String fieldName) {
        return nameFields.get(fieldName);
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
        if (StringUtils.isBlank(language) || category == Category.data) {
            return;
        }
        Pair<Set<String>, Boolean> pair = Language.parse(language);
        languages = pair.getLeft();
        excludeLanguage = pair.getRight();
    }

    public boolean isExcludeLanguage() {
        return excludeLanguage;
    }

    public void setExcludeLanguage(boolean excludeLanguage) {
        this.excludeLanguage = excludeLanguage;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public Map<String, String> getImports() {
        return imports;
    }

    public boolean isSupportLanguage(Language language) {
        boolean support = languages.isEmpty() || languages.contains(language.name());
        if (excludeLanguage) {
            support = !support;
        }
        return support;
    }


    public boolean isSupportLanguage(String language) {
        return isSupportLanguage(Language.valueOf(language));
    }

    public Set<String> getSupportedLanguages() {
        if (supportedLanguages != null) {
            return supportedLanguages;
        }
        supportedLanguages = new HashSet<>();
        for (Language language : Language.values()) {
            if (isSupportLanguage(language)) {
                supportedLanguages.add(language.name());
            }
        }
        return supportedLanguages;
    }

    public void addDependent(DependentType dependentType, ClassDefinition ownerClass, Definition ownerDefinition, ClassDefinition dependentClass) {
        if (dependentClass == null) {
            return;
        }
        DependentSource dependentSource = new DependentSource(dependentType, ownerClass, ownerDefinition, dependentClass);
        dependentsClasses.computeIfAbsent(dependentClass.getName(), k -> new TreeMap<>()).put(dependentSource, dependentClass);
    }

    public Map<String, TreeMap<DependentSource, ClassDefinition>> getDependentsClasses() {
        return dependentsClasses;
    }

    public void validate1() {
        validateNameAndLanguage();

        for (FieldDefinition fieldDefinition : getFields()) {
            validateField(fieldDefinition);
        }
    }

    /**
     * 依赖{@link #validate1()}的结果，必须等所有类的{@link #validate1()}执行完成后再执行
     */
    public void validate2() {
    }

    /**
     * 依赖{@link #validate2()}的结果
     */
    public void validate3() {
        validateDependents();
    }

    protected void validateNameAndLanguage() {
        if (getName() == null) {
            addValidatedError(getKindName() + "名不能为空");
        } else if (!getNamePattern().matcher(getName()).matches()) {
            addValidatedError(getKindName() + "[" + getName() + "]名字格式错误,正确格式:" + getNamePattern());
        }

        if (!languages.isEmpty() && !Language.names().containsAll(languages)) {
            addValidatedError(getValidatedName() + "的语言类型" + languages + "非法,合法的语言类型" + Language.names());
        }
    }

    protected void validateField(FieldDefinition fieldDefinition) {
        validateFieldName(fieldDefinition);
        validateFieldNameDuplicate(fieldDefinition);
    }

    protected void validateFieldName(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            addValidatedError(getValidatedName("的") + "字段名不能为空");
            return;
        }

        //校验字段名格式
        if (!fieldDefinition.getNamePattern().matcher(fieldDefinition.getName()).matches()) {
            addValidatedError(getValidatedName("的") + "字段名[" + fieldDefinition.getName() + "]格式错误,正确格式:" + fieldDefinition.getNamePattern());
            return;
        }

        if (isReservedWord(fieldDefinition.getName())) {
            addValidatedError(getValidatedName("的") + "字段名[" + fieldDefinition.getName() + "]不合法，不能使用保留字");
        }
    }

    protected boolean isReservedWord(String fieldName) {
        if (category == Category.data) {
            return Language.java.reservedWords().contains(fieldName);
        }
        if (Language.java.reservedWords().contains(fieldName)) {
            return true;
        }
        if (Language.cs.reservedWords().contains(fieldName)) {
            return true;
        }
        return Language.lua.reservedWords().contains(fieldName);
    }

    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            return;
        }
        if (nameFields.containsKey(fieldDefinition.getName())) {
            addValidatedError(getValidatedName("的") + "字段名[" + fieldDefinition.getName() + "]不能重复");
        } else {
            nameFields.put(fieldDefinition.getName(), fieldDefinition);
        }
    }

    protected void validateDependents() {
    }

    protected void addValidatedError(String error) {
        addValidatedError(error, null);
    }

    protected void addValidatedError(String error, ClassDefinition other) {
        String position = ",所在定义文件:" + getDefinitionFile();
        if (other != null) {
            position = "和" + other.getDefinitionFile();
        }

        error += position;
        parser.addValidatedError(error);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + getLongName() + '\'' +
                '}';
    }

}
