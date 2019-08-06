package quan.generator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;

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

    public DefinitionParser setCategory(DefinitionCategory category) {
        this.category = category;
        return this;
    }

    public DefinitionCategory getCategory() {
        return category;
    }

    public void setDefinitionPaths(List<String> definitionPaths) {
        for (String path : definitionPaths) {
            path = PathUtils.crossPlatPath(path);
            this.definitionPaths.add(path);
            Set<File> files = PathUtils.listFiles(new File(path), getFileType());
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

    protected abstract String getFileType();

    public void parse() throws Exception {
        if (!ClassDefinition.getClasses().isEmpty()) {
            return;
        }
        List<ClassDefinition> classDefinitions = new ArrayList<>();

        for (File definitionFile : definitionFiles) {
            classDefinitions.addAll(parseClasses(definitionFile));
        }

        for (File definitionFile : definitionFiles) {
            parseFields(definitionFile);
        }

        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition.getName() == null) {
                continue;
            }
            ClassDefinition otherClassDefinition = ClassDefinition.getClass(classDefinition.getName());
            if (otherClassDefinition != null) {
                String error = "定义文件[" + classDefinition.getDefinitionFile();
                error += "]和[" + otherClassDefinition.getDefinitionFile();
                error += "]有同名类[" + classDefinition.getName() + "]";
                ClassDefinition.getValidatedErrors().add(error);
            } else {
                ClassDefinition.getClasses().put(classDefinition.getName(), classDefinition);
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

    protected abstract void parseFields(File definitionFile) throws Exception;

}
