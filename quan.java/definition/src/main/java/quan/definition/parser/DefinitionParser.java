package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.PathUtils;
import quan.definition.BeanDefinition;
import quan.definition.Category;
import quan.definition.ClassDefinition;
import quan.definition.config.ConfigDefinition;
import quan.definition.message.HeadDefinition;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by quanchangnai on 2019/7/9.
 */
public abstract class DefinitionParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Category category;

    protected String packagePrefix;

    protected String enumPackagePrefix;

    protected String definitionFileEncoding = Charset.defaultCharset().name();

    private LinkedHashSet<String> definitionPaths = new LinkedHashSet<>();

    protected LinkedHashSet<File> definitionFiles = new LinkedHashSet<>();

    //解析出来的类定义
    protected List<ClassDefinition> parsedClasses = new ArrayList<>();

    //消息头定义，最多只能有一个
    protected HeadDefinition headDefinition;

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
            path = PathUtils.toPlatPath(path);
            this.definitionPaths.add(path);
            Set<File> files = PathUtils.listFiles(new File(path), definitionFileType());
            definitionFiles.addAll(files);
        }
    }

    public void setDefinitionPath(String definitionPath) {
        setDefinitionPaths(Collections.singletonList(definitionPath));
    }

    public void setPackagePrefix(String packagePrefix) {
        if (StringUtils.isBlank(packagePrefix)) {
            return;
        }
        this.packagePrefix = packagePrefix;
    }

    public void setEnumPackagePrefix(String enumPackagePrefix) {
        if (StringUtils.isBlank(enumPackagePrefix)) {
            return;
        }
        this.enumPackagePrefix = enumPackagePrefix;
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

    public ClassDefinition getClass(String name) {
        return validatedClasses.get(name);
    }

    public HeadDefinition getMessageHead() {
        return headDefinition;
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

    protected abstract String definitionFileType();

    public void parse() {
        if (!validatedClasses.isEmpty()) {
            return;
        }

        definitionFiles.forEach(this::parseClasses);

        Map<String, ClassDefinition> dissimilarClasses = new HashMap<>();

        for (ClassDefinition classDefinition : parsedClasses) {
            if (classDefinition.getName() == null) {
                continue;
            }
            ClassDefinition existingClassDefinition = validatedClasses.get(classDefinition.getName());
            if (existingClassDefinition != null) {
                String error = "定义文件[" + classDefinition.getDefinitionFile() + "]";
                if (!classDefinition.getDefinitionFile().equals(existingClassDefinition.getDefinitionFile())) {
                    error += "和[" + existingClassDefinition.getDefinitionFile() + "]";
                }
                error += "有同名类[" + classDefinition.getName() + "]";
                validatedErrors.add(error);
            } else {
                validatedClasses.put(classDefinition.getName(), classDefinition);
            }

            ClassDefinition similarClassDefinition = dissimilarClasses.get(classDefinition.getName().toLowerCase());
            if (similarClassDefinition != null && !similarClassDefinition.getName().equals(classDefinition.getName())) {
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

        for (ClassDefinition classDefinition : parsedClasses) {
            classDefinition.validate();
        }

        for (ClassDefinition classDefinition : parsedClasses) {
            classDefinition.validate2();
        }
    }

    public void clear() {
        parsedClasses.clear();
        validatedClasses.clear();
        validatedErrors.clear();
        tableConfigs.clear();
    }

    protected abstract void parseClasses(File definitionFile);

}
