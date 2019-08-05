package quan.generator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/9.
 */
public abstract class DefinitionParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Definition.Category category;

    protected String packagePrefix;

    protected String enumPackagePrefix;

    private List<String> definitionPaths = new ArrayList<>();

    protected List<File> definitionFiles = new ArrayList<>();

    public DefinitionParser setCategory(Definition.Category category) {
        this.category = category;
        return this;
    }

    public Definition.Category getCategory() {
        return category;
    }

    public void setDefinitionPaths(List<String> definitionPaths) {
        for (String path : definitionPaths) {
            path = PathUtils.crossPlatPath(path);
            this.definitionPaths.add(path);
            File file = new File(path);
            File[] files = file.listFiles((File dir, String name) -> name.endsWith("." + getFileType()));
            if (files != null) {
                definitionFiles.addAll(Arrays.asList(files));
            }
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

        for (File srcFile : definitionFiles) {
            classDefinitions.addAll(parseClasses(srcFile));
        }

        for (File srcFile : definitionFiles) {
            parseFields(srcFile);
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

    protected abstract List<ClassDefinition> parseClasses(File srcFile) throws Exception;

    protected abstract void parseFields(File srcFile) throws Exception;

}
