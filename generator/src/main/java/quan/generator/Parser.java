package quan.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/9.
 */
public abstract class Parser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String srcPath;

    protected String packagePrefix;

    protected List<File> srcFiles = new ArrayList<>();

    protected Map<String, ClassDefinition> classDefinitions = new HashMap<>();

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }


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
