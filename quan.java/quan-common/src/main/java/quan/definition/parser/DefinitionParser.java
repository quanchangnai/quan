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

/**
 * Created by quanchangnai on 2019/7/9.
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
            Set<File> files = CommonUtils.listFiles(new File(path), getDefinitionType());
            definitionFiles.addAll(files);
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

        definitionFiles.forEach(this::parseFile);

        validate();
    }

    protected void validate() {
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

        parsedClasses.forEach(ClassDefinition::validate1);
        parsedClasses.forEach(ClassDefinition::validate2);
        parsedClasses.forEach(ClassDefinition::validate3);
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

    protected abstract void parseFile(File definitionFile);

}
