package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.definition.BeanDefinition;
import quan.definition.ClassDefinition;
import quan.definition.DefinitionCategory;
import quan.definition.config.ConfigDefinition;
import quan.definition.message.MessageHeadDefinition;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2019/7/9.
 */
public abstract class DefinitionParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DefinitionCategory category;

    protected String packagePrefix;

    protected String enumPackagePrefix;

    private LinkedHashSet<String> definitionPaths = new LinkedHashSet<>();

    protected LinkedHashSet<File> definitionFiles = new LinkedHashSet<>();

    //解析出来的类定义
    protected List<ClassDefinition> parsedClasses = new ArrayList<>();

    //消息头定义，最多只能有一个
    protected MessageHeadDefinition messageHeadDefinition;

    //已校验过的类定义，类名:类定义
    private Map<String, ClassDefinition> validatedClasses = new HashMap<>();

    //校验出的错误信息
    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

    //表名:配置
    private Map<String, ConfigDefinition> tableConfigs = new HashMap<>();

    public DefinitionParser setCategory(DefinitionCategory category) {
        this.category = category;
        return this;
    }

    public DefinitionCategory getCategory() {
        return category;
    }

    public void setDefinitionPaths(List<String> definitionPaths) {
        for (String path : definitionPaths) {
            path = PathUtils.currentPlatPath(path);
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

    public MessageHeadDefinition getMessageHead() {
        return messageHeadDefinition;
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

        for (ClassDefinition classDefinition : parsedClasses) {
            if (classDefinition.getName() == null) {
                continue;
            }
            ClassDefinition otherClassDefinition = validatedClasses.get(classDefinition.getName());
            if (otherClassDefinition != null) {
                String error = "定义文件[" + classDefinition.getDefinitionFile();
                error += "]和[" + otherClassDefinition.getDefinitionFile();
                error += "]有同名类[" + classDefinition.getName() + "]";
                validatedErrors.add(error);
            } else {
                validatedClasses.put(classDefinition.getName(), classDefinition);
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
