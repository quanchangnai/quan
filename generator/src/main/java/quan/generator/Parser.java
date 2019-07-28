package quan.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created by quanchangnai on 2019/7/9.
 */
public abstract class Parser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String packagePrefix;

    protected String enumPackagePrefix;

    protected List<File> srcFiles = new ArrayList<>();

    protected Map<String, ClassDefinition> classDefinitions = new HashMap<>();

    public void setSrcPaths(List<String> srcPaths) {
        for (String srcPath : srcPaths) {
            File file = new File(srcPath);
            File[] files = file.listFiles((File dir, String name) -> name.endsWith("." + getSrcFileType()));
            if (files != null) {
                srcFiles.addAll(Arrays.asList(files));
            }
        }
    }

    public void setSrcPath(String srcPath) {
        setSrcPaths(Collections.singletonList(srcPath));
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public void setEnumPackagePrefix(String enumPackagePrefix) {
        this.enumPackagePrefix = enumPackagePrefix;
    }

    protected abstract String getSrcFileType();

    public void parse() throws Exception {
        for (File srcFile : srcFiles) {
            parseClasses(srcFile);
        }

        ClassDefinition.getAll().putAll(classDefinitions);

        for (File srcFile : srcFiles) {
            parseFields(srcFile);
        }

        for (ClassDefinition classDefinition : classDefinitions.values()) {
            classDefinition.validate();
        }

    }

    protected abstract void parseClasses(File srcFile) throws Exception;

    protected abstract void parseFields(File srcFile) throws Exception;

    protected void addClassDefinition(ClassDefinition classDefinition) {
        ClassDefinition existsClassDefinition = classDefinitions.get(classDefinition.getName());
        if (existsClassDefinition != null) {
            String errorStr = "定义文件[" + classDefinition.getDefinitionFile();
            errorStr += "]和[" + existsClassDefinition.getDefinitionFile();
            errorStr += "]有同名类[" + classDefinition.getName() + "]";

            throw new RuntimeException(errorStr);

        }

        classDefinitions.put(classDefinition.getName(), classDefinition);
    }

}
