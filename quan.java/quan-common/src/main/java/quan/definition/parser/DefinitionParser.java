package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.config.ConfigDefinition;
import quan.util.CommonUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 【定义】解析器
 */
public abstract class DefinitionParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Category category;

    //包名前缀，没有区分具体语言，切换语言的时候需要重新设置
    protected String packagePrefix;

    protected String enumPackagePrefix;

    protected String definitionFileEncoding;

    private LinkedHashSet<String> definitionPaths = new LinkedHashSet<>();

    protected LinkedHashSet<File> definitionFiles = new LinkedHashSet<>();

    private Pattern enumNamePattern;

    private Pattern beanNamePattern;

    private Pattern messageNamePattern;

    private Pattern dataNamePattern;

    private Pattern configNamePattern;

    //配置常量类名格式
    private Pattern constantNamePattern;

    //解析出来的类定义
    protected List<ClassDefinition> parsedClasses = new ArrayList<>();

    //已校验过的类定义，类名:类定义
    private Map<String, ClassDefinition> validatedClasses = new HashMap<>();

    //校验出的错误信息
    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    //表名:配置
    private Map<String, ConfigDefinition> tableConfigs = new HashMap<>();


    public DefinitionParser setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public void setDefinitionFileEncoding(String definitionFileEncoding) {
        if (!StringUtils.isBlank(definitionFileEncoding)) {
            this.definitionFileEncoding = definitionFileEncoding;
        }
    }

    public void setDefinitionPaths(Collection<String> definitionPaths) {
        for (String path : definitionPaths) {
            path = CommonUtils.toPlatPath(path);
            this.definitionPaths.add(path);
            definitionFiles.addAll(CommonUtils.listFiles(new File(path)));
        }
    }

    public void setDefinitionPath(String definitionPath) {
        setDefinitionPaths(Collections.singletonList(definitionPath));
    }

    public void setPackagePrefix(String packagePrefix) {
        if (!StringUtils.isBlank(packagePrefix)) {
            this.packagePrefix = packagePrefix;
        }
    }

    public void setEnumPackagePrefix(String enumPackagePrefix) {
        if (!StringUtils.isBlank(enumPackagePrefix)) {
            this.enumPackagePrefix = enumPackagePrefix;
        }
    }

    public LinkedHashSet<String> getDefinitionPaths() {
        return definitionPaths;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public String getEnumPackagePrefix() {
        return enumPackagePrefix;
    }


    public Pattern getEnumNamePattern() {
        return enumNamePattern;
    }

    public Pattern getBeanNamePattern() {
        return beanNamePattern;
    }

    public Pattern getMessageNamePattern() {
        return messageNamePattern;
    }

    public Pattern getDataNamePattern() {
        return dataNamePattern;
    }

    public Pattern getConfigNamePattern() {
        return configNamePattern;
    }

    public Pattern getConstantNamePattern() {
        return constantNamePattern;
    }

    public void setEnumNamePattern(String enumNamePattern) {
        if (!StringUtils.isBlank(enumNamePattern)) {
            this.enumNamePattern = Pattern.compile(enumNamePattern);
        }
    }

    public void setBeanNamePattern(String beanNamePattern) {
        if (!StringUtils.isBlank(beanNamePattern)) {
            this.beanNamePattern = Pattern.compile(beanNamePattern);
        }
    }

    public void setMessageNamePattern(String messageNamePattern) {
        if (!StringUtils.isBlank(messageNamePattern)) {
            this.messageNamePattern = Pattern.compile(messageNamePattern);
        }
    }

    public void setDataNamePattern(String dataNamePattern) {
        if (!StringUtils.isBlank(dataNamePattern)) {
            this.dataNamePattern = Pattern.compile(dataNamePattern);
        }
    }

    public void setConfigNamePattern(String configNamePattern) {
        if (!StringUtils.isBlank(configNamePattern)) {
            this.configNamePattern = Pattern.compile(configNamePattern);
        }
    }

    public void setConstantNamePattern(String constantNamePattern) {
        if (!StringUtils.isBlank(constantNamePattern)) {
            this.constantNamePattern = Pattern.compile(constantNamePattern);
        }
    }

    public Map<String, ClassDefinition> getClasses() {
        return validatedClasses;
    }

    /**
     * 通过[与语言无关的的包名.类名]获取类定义
     */
    public ClassDefinition getClass(String name) {
        return validatedClasses.get(name);
    }

    public ConfigDefinition getConfig(String name) {
        ClassDefinition classDefinition = validatedClasses.get(name);
        if (classDefinition instanceof ConfigDefinition) {
            return (ConfigDefinition) classDefinition;
        }
        return null;
    }

    public Map<String, ConfigDefinition> getTableConfigs() {
        return tableConfigs;
    }

    public BeanDefinition getBean(String name) {
        ClassDefinition classDefinition = validatedClasses.get(name);
        if (classDefinition instanceof BeanDefinition) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }

    public void addValidatedError(String error) {
        validatedErrors.add(error);
    }

    public LinkedHashSet<String> getValidatedErrors() {
        return validatedErrors;
    }

    public abstract String getDefinitionType();

    public void parse() {
        Objects.requireNonNull(category);
        if (!validatedClasses.isEmpty()) {
            return;
        }

        for (File definitionFile : definitionFiles) {
            if (checkFile(definitionFile)) {
                try {
                    parseFile(definitionFile);
                } catch (Exception e) {
                    logger.error("定义文件[{}]解析出错", definitionFile.getName(), e);
                    addValidatedError(String.format("定义文件[%s]解析出错：%s", definitionFile.getName(), e.getMessage()));
                }
            }
        }

        validate();
    }

    protected boolean checkFile(File definitionFile) {
        return definitionFile.getName().endsWith(getDefinitionType());
    }


    protected abstract void parseFile(File definitionFile);

    protected void validate() {
        validateClassName();

        parsedClasses.forEach(ClassDefinition::validate1);
        parsedClasses.forEach(ClassDefinition::validate2);
        parsedClasses.forEach(ClassDefinition::validate3);
    }

    protected void validateClassName() {
        Map<String, ClassDefinition> dissimilarClasses = new HashMap<>();

        for (ClassDefinition classDefinition : parsedClasses) {
            if (classDefinition.getName() == null) {
                continue;
            }

            ClassDefinition validatedClassDefinition = validatedClasses.get(classDefinition.getLongName());
            if (validatedClassDefinition != null) {
                String error = "定义文件[" + classDefinition.getDefinitionFile() + "]";
                if (!classDefinition.getDefinitionFile().equals(validatedClassDefinition.getDefinitionFile())) {
                    error += "和[" + validatedClassDefinition.getDefinitionFile() + "]";
                }
                error += "有同名类[" + classDefinition.getName() + "]";
                validatedErrors.add(error);
            } else {
                validatedClasses.put(classDefinition.getLongName(), classDefinition);
            }

            ClassDefinition similarClassDefinition = dissimilarClasses.get(classDefinition.getLongName().toLowerCase());
            if (similarClassDefinition != null && !similarClassDefinition.getLongName().equals(classDefinition.getLongName())) {
                String error = "定义文件[" + classDefinition.getDefinitionFile() + "]的类[" + similarClassDefinition.getName() + "]和";
                if (!classDefinition.getDefinitionFile().equals(similarClassDefinition.getDefinitionFile())) {
                    error += "[" + similarClassDefinition.getDefinitionFile() + "]的";
                }
                error += "类[" + classDefinition.getName() + "]名字相似";
                validatedErrors.add(error);
            } else {
                dissimilarClasses.put(classDefinition.getName().toLowerCase(), classDefinition);
            }
        }
    }

    public void clear() {
        parsedClasses.clear();
        validatedClasses.clear();
        validatedErrors.clear();
        tableConfigs.clear();
    }

    public static DefinitionParser createParser(String definitionType) {
        switch (definitionType) {
            case "csv":
                return new CSVDefinitionParser();
            case "xls":
            case "xlsx":
                return new ExcelDefinitionParser(definitionType);
            default://xml
                return new XmlDefinitionParser();
        }
    }

}
