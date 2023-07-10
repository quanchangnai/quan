package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.config.ConfigDefinition;
import quan.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    protected LinkedHashSet<String> definitionPaths = new LinkedHashSet<>();

    protected LinkedHashSet<File> definitionFiles = new LinkedHashSet<>();

    //定义文件的相对路径名
    protected Map<File, String> definitionFilePaths = new HashMap<>();

    private Pattern enumNamePattern;

    private Pattern beanNamePattern;

    private Pattern messageNamePattern;

    private Pattern dataNamePattern;

    private Pattern configNamePattern;

    //配置常量类名格式
    private Pattern constantNamePattern;

    //解析出来的类定义，还未校验类名
    protected List<ClassDefinition> parsedClasses = new ArrayList<>();

    //key:长类名
    private Map<String, ClassDefinition> longName2Classes = new HashMap<>();

    //key:短类名
    private Map<String, Set<ClassDefinition>> shortName2Classes = new HashMap<>();

    //校验出的错误信息
    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    //key:表格文件名
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
            path = FileUtils.toPlatPath(path);
            this.definitionPaths.add(path);
            Path definitionPath = Paths.get(path);

            Set<File> definitionFiles = FileUtils.listFiles(new File(path));
            this.definitionFiles.addAll(definitionFiles);
            for (File definitionFile : definitionFiles) {
                Path relativizedPath = definitionPath.relativize(Paths.get(definitionFile.getPath()));
                definitionFilePaths.put(definitionFile, relativizedPath.toString());
            }
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

    /**
     * 获取所有的类定义
     */
    public Collection<ClassDefinition> getClasses() {
        return longName2Classes.values();
    }

    /**
     * 通过长类名获取类定义
     */
    public ClassDefinition getClass(String longName) {
        return longName2Classes.get(longName);
    }

    /**
     * 通过短类名获取类定义
     */
    public Set<ClassDefinition> getClasses(String shortName) {
        return shortName2Classes.get(shortName);
    }


    public ClassDefinition getClass(ClassDefinition owner, String name) {
        ClassDefinition classDefinition = getClass(ClassDefinition.getLongName(owner, name));
        if (classDefinition == null) {
            classDefinition = getClass(name);
        }
        return classDefinition;
    }

    public ConfigDefinition getConfig(String name) {
        ClassDefinition classDefinition = longName2Classes.get(name);
        if (classDefinition instanceof ConfigDefinition) {
            return (ConfigDefinition) classDefinition;
        }
        return null;
    }

    public ConfigDefinition getConfig(ClassDefinition owner, String name) {
        ConfigDefinition configDefinition = getConfig(ClassDefinition.getLongName(owner, name));
        if (configDefinition == null) {
            configDefinition = getConfig(name);
        }
        return configDefinition;
    }

    public Map<String, ConfigDefinition> getTableConfigs() {
        return tableConfigs;
    }

    public BeanDefinition getBean(String name) {
        ClassDefinition classDefinition = longName2Classes.get(name);
        if (classDefinition instanceof BeanDefinition) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }

    public BeanDefinition getBean(ClassDefinition owner, String name) {
        BeanDefinition beanDefinition = getBean(ClassDefinition.getLongName(owner, name));
        if (beanDefinition == null) {
            beanDefinition = getBean(name);
        }
        return beanDefinition;
    }

    public void addValidatedError(String error) {
        validatedErrors.add(error);
    }

    public LinkedHashSet<String> getValidatedErrors() {
        return validatedErrors;
    }

    public abstract String getDefinitionType();

    /**
     * 最小表格正文起始行号
     */
    public abstract int getMinTableBodyStartRow();

    public void parse() {
        Objects.requireNonNull(category);
        if (!longName2Classes.isEmpty()) {
            return;
        }

        for (File definitionFile : definitionFiles) {
            if (checkFile(definitionFile)) {
                try {
                    parseFile(definitionFile);
                } catch (Exception e) {
                    logger.error("定义文件[{}]解析出错", definitionFile, e);
                    addValidatedError(String.format("定义文件[%s]解析出错：%s", definitionFile, e.getMessage()));
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

    private void addNameError(ClassDefinition classDefinition1, ClassDefinition classDefinition2, String append) {
        String error = classDefinition1.getValidatedName("和") + classDefinition2.getValidatedName();

        if (append != null) {
            error += append;
        }

        validatedErrors.add(error);
    }

    protected void validateClassName() {
        Map<String, ClassDefinition> dissimilarNameClasses = new HashMap<>();

        for (ClassDefinition classDefinition1 : parsedClasses) {
            if (classDefinition1.getName() == null) {
                continue;
            }

            if (shortName2Classes.containsKey(classDefinition1.getName())) {
                for (ClassDefinition classDefinition2 : shortName2Classes.get(classDefinition1.getName())) {
                    if (!classDefinition1.isAllowSameName() && !classDefinition2.isAllowSameName()) {
                        addNameError(classDefinition1, classDefinition2, "名字相同");
                    }
                }
            }
            shortName2Classes.computeIfAbsent(classDefinition1.getName(), k -> new HashSet<>()).add(classDefinition1);

            ClassDefinition classDefinition3 = longName2Classes.get(classDefinition1.getLongName());
            if (classDefinition3 == null) {
                longName2Classes.put(classDefinition1.getLongName(), classDefinition1);
            } else {
                addNameError(classDefinition1, classDefinition3, "名字相同");
            }

            ClassDefinition classDefinition4 = dissimilarNameClasses.get(classDefinition1.getLongName().toLowerCase());
            if (classDefinition4 == null) {
                dissimilarNameClasses.put(classDefinition1.getLongName().toLowerCase(), classDefinition1);
            } else if (!classDefinition1.getLongName().equals(classDefinition4.getLongName())) {
                addNameError(classDefinition1, classDefinition4, "名字相似");
            }
        }
    }

    public void clear() {
        definitionFilePaths.clear();
        parsedClasses.clear();
        longName2Classes.clear();
        validatedErrors.clear();
        tableConfigs.clear();
    }

    public static DefinitionParser createParser(String definitionType) {
        switch (definitionType.trim()) {
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
