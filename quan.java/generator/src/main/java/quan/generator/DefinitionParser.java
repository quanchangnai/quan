package quan.generator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;
import quan.generator.config.ConfigDefinition;

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

    private List<String> definitionPaths = new ArrayList<>();

    protected LinkedHashSet<File> definitionFiles = new LinkedHashSet<>();

    private Map<String, ClassDefinition> classes = new HashMap<>();

    //表名:配置
    private Map<String, ConfigDefinition> tableConfigs = new HashMap<>();

    private LinkedHashSet<String> validatedErrors = new LinkedHashSet<>();

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

    public List<String> getDefinitionPaths() {
        return definitionPaths;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public String getEnumPackagePrefix() {
        return enumPackagePrefix;
    }

    public Map<String, ClassDefinition> getClasses() {
        return classes;
    }

    public ClassDefinition getClass(String name) {
        return classes.get(name);
    }

    public ConfigDefinition getConfig(String name) {
        ClassDefinition classDefinition = classes.get(name);
        if (classDefinition instanceof ConfigDefinition) {
            return (ConfigDefinition) classDefinition;
        }
        return null;
    }

    public Map<String, ConfigDefinition> getTableConfigs() {
        return tableConfigs;
    }

    public BeanDefinition getBean(String name) {
        ClassDefinition classDefinition = classes.get(name);
        if (classDefinition instanceof BeanDefinition) {
            return (BeanDefinition) classDefinition;
        }
        return null;
    }


    public void addValidatedError(String error) {
        validatedErrors.add(error);
    }

    public List<String> getValidatedErrors() {
        return new ArrayList<>(validatedErrors);
    }

    protected abstract String definitionFileType();

    public void parse() throws Exception {
        if (!classes.isEmpty()) {
            return;
        }
        List<ClassDefinition> classDefinitions = new ArrayList<>();

        for (File definitionFile : definitionFiles) {
            classDefinitions.addAll(parseClasses(definitionFile));
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition.getName() == null) {
                continue;
            }
            ClassDefinition otherClassDefinition = classes.get(classDefinition.getName());
            if (otherClassDefinition != null) {
                String error = "定义文件[" + classDefinition.getDefinitionFile();
                error += "]和[" + otherClassDefinition.getDefinitionFile();
                error += "]有同名类[" + classDefinition.getName() + "]";
                validatedErrors.add(error);
            } else {
                classes.put(classDefinition.getName(), classDefinition);
            }
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            classDefinition.validate();
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            classDefinition.validate2();
        }
    }

    protected abstract List<ClassDefinition> parseClasses(File definitionFile) throws Exception;

}
