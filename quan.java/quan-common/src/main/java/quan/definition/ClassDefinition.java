package quan.definition;

import org.apache.commons.lang3.StringUtils;
import quan.definition.DependentSource.DependentType;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 类定义
 */
public abstract class ClassDefinition extends Definition {

    public static final Pattern NAME_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\d]*");

    //不含前缀的默认包名
    private String packageName;

    //不同语言对应的不含前缀的包名，为空时使用默认包名
    private Map<String, String> packageNames = new HashMap<>();

    //定义文件
    private String definitionFile;

    //定义版本
    private String version;

    private String languageStr;

    //支持的语言
    protected Set<String> languages = new HashSet<>();

    //依赖的类，Map<依赖的类名, <来源, ClassDefinition>>
    protected Map<String, TreeMap<DependentSource, ClassDefinition>> dependentsClasses = new HashMap<>();

    protected List<FieldDefinition> fields = new LinkedList<>();

    //当前语言
    protected Language currentLanguage;

    //导包，和具体语言相关
    private Map<String, String> imports = new TreeMap<>();

    //依赖类的简单名对应全名，和具体语言相关
    private Map<String, String> dependentClassNames;

    //字段名:字段定义
    protected Map<String, FieldDefinition> nameFields = new HashMap<>();

    @Override
    public String getKindName() {
        return "类型";
    }

    @Override
    public Pattern getNamePattern() {
        return NAME_PATTERN;
    }

    public void reset() {
        currentLanguage = null;
        imports.clear();
        dependentClassNames = null;
        fields.forEach(this::resetField);
    }

    protected void resetField(FieldDefinition fieldDefinition) {
        fieldDefinition.setBasicType(null);
        fieldDefinition.setClassType(null);
        fieldDefinition.setKeyBasicType(null);
        fieldDefinition.setKeyClassType(null);
        fieldDefinition.setValueBasicType(null);
        fieldDefinition.setValueClassType(null);
        fieldDefinition.setRefType(null);
    }

    public String getPackagePrefix() {
        if (this instanceof EnumDefinition && !isBlank(parser.getEnumPackagePrefix())) {
            return parser.getEnumPackagePrefix();
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
        return getLongName(this, getName());
    }

    public String getLongName(Language lang) {
        String packageName = getPackageName(lang);
        if (StringUtils.isBlank(packageName)) {
            return getName();
        }
        return packageName + "." + getName();
    }


    @Override
    public String getValidatedName(String append) {
        String kindName = getKindName();
        String validatedName;
        if (getName() != null) {
            validatedName = kindName + "[" + getLongName() + "]" + append;
        } else {
            validatedName = kindName + append;
        }
        return validatedName;
    }

    /**
     * 短类名是否允许相同
     */
    public boolean isAllowSameName() {
        return true;
    }

    /**
     * 获取短类名，即不带包名的类名
     */
    public static String getShortName(String name) {
        if (name == null) {
            return null;
        }
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            return name.substring(index + 1);
        }
        return name;
    }


    /**
     * 获取长类名，即和具体语言环境无关的[不含前缀的包名.类名]
     */
    public static String getLongName(ClassDefinition owner, String name) {
        if (!isBlank(owner.getPackageName()) && !isBlank(name) && !name.contains(".")) {
            name = owner.getPackageName() + "." + name;
        }
        return name;
    }

    public String getFullPackageName(String lang) {
        String packagePrefix = getPackagePrefix();
        String packageName = getPackageName(lang);
        if (!isBlank(packagePrefix)) {
            if (!isBlank(packageName)) {
                return packagePrefix + "." + packageName;
            } else {
                return packagePrefix;
            }
        }
        return packageName;
    }

    public String getFullPackageName(Language lang) {
        return getFullPackageName(lang.name());
    }

    public String getFullName(String lang) {
        String fullPackageName = getFullPackageName(lang);
        if (StringUtils.isBlank(fullPackageName)) {
            return getName();
        } else {
            return fullPackageName + "." + getName();
        }
    }

    public String getFullName(Language lang) {
        return getFullName(lang.name());
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public void setLanguageStr(String languageStr) {
        this.languageStr = languageStr;
    }

    public String getLanguageStr() {
        return languageStr;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public Map<String, String> getImports() {
        return imports;
    }

    public void addImport(String importName) {
        if (currentLanguage == Language.java && importName.equals(getFullPackageName(currentLanguage) + ".*")
                || currentLanguage == Language.cs && importName.equals(getFullPackageName(currentLanguage))) {
            return;
        }
        imports.put(importName, null);
    }

    public boolean isSupportedLanguage(Language language) {
        return languages.contains(language.name());
    }


    public boolean isSupportedLanguage(String language) {
        return isSupportedLanguage(Language.valueOf(language));
    }

    public void addDependent(DependentType dependentType, ClassDefinition ownerClass, Definition ownerDefinition, ClassDefinition dependentClass) {
        if (dependentClass != null) {
            DependentSource dependentSource = new DependentSource(dependentType, ownerClass, ownerDefinition, dependentClass);
            dependentsClasses.computeIfAbsent(dependentClass.getName(), k -> new TreeMap<>()).put(dependentSource, dependentClass);
        }
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
        for (FieldDefinition fieldDefinition : getFields()) {
            validateFieldNameDuplicate(fieldDefinition);
        }
    }

    /**
     * 依赖{@link #validate2()}的结果
     */
    public void validate3() {
        validateDependents();
    }

    protected void validateNameAndLanguage() {
        if (getName() == null) {
            addValidatedError("定义文件[" + getDefinitionFile() + "]中" + getKindName() + "的名字不能为空");
        } else {
            if (isReservedWord(getName())) {
                addValidatedError(getValidatedName() + "的名字不合法,不能使用保留字");
            }
            if (!getNamePattern().matcher(getName()).matches()) {
                addValidatedError(getValidatedName() + "的名字格式错误,正确格式:" + getNamePattern());
            }
            if (getIllegalNames().contains(getName())) {
                addValidatedError(getValidatedName() + "的名字不合法");
            }
        }

        try {
            languages = Language.parse(languageStr);
        } catch (IllegalArgumentException e) {
            addValidatedError(getValidatedName() + "的语言约束[" + languageStr + "]非法,合法的语言类型" + Language.names());
        }
    }

    protected Set<String> getIllegalNames() {
        return Collections.emptySet();
    }

    protected void validateField(FieldDefinition fieldDefinition) {
        validateFieldName(fieldDefinition);
    }

    /**
     * 校验字段名
     */
    protected void validateFieldName(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            addValidatedError(getValidatedName() + "的字段名不能为空");
            return;
        }

        //校验字段名格式
        if (!fieldDefinition.getNamePattern().matcher(fieldDefinition.getName()).matches()) {
            addValidatedError(getValidatedName() + "的字段名[" + fieldDefinition.getName() + "]格式错误,正确格式:" + fieldDefinition.getNamePattern());
            return;
        }

        if (isReservedWord(fieldDefinition.getName())) {
            addValidatedError(getValidatedName() + "的字段名[" + fieldDefinition.getName() + "]不合法，不能使用保留字");
        }
    }

    protected boolean isReservedWord(String name) {
        if (category == Category.data) {
            return Language.java.reservedWords().contains(name);
        }
        if (Language.java.reservedWords().contains(name)) {
            return true;
        }
        if (Language.cs.reservedWords().contains(name)) {
            return true;
        }
        return Language.lua.reservedWords().contains(name);
    }

    protected void validateFieldNameDuplicate(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getName() == null) {
            return;
        }
        if (nameFields.containsKey(fieldDefinition.getName())) {
            addValidatedError(getValidatedName() + "的字段名[" + fieldDefinition.getName() + "]不能重复");
        } else {
            nameFields.put(fieldDefinition.getName(), fieldDefinition);
        }
    }

    protected void validateDependents() {
    }

    protected void addValidatedError(String error) {
        parser.addValidatedError(error);
    }

    public String resolveFieldRef(String fieldRef) {
        return fieldRef;
    }

    public void setDependentClassNames(Map<String, String> dependentClassNames) {
        this.dependentClassNames = dependentClassNames;
    }

    public String getDependentName(String className) {
        if (dependentClassNames != null && getName().equals(className) && dependentClassNames.containsKey(className)) {
            return dependentClassNames.get(className);
        } else {
            return className;
        }
    }

    public String dn(String className) {
        return getDependentName(className);
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + getLongName() + '\'' +
                '}';
    }

}
