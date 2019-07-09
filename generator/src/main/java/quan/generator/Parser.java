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

    protected Map<String, ClassDefinition> results = new HashMap<>();

    public String getSrcPath() {
        return srcPath;
    }

    public Parser setSrcPath(String srcPath) {
        this.srcPath = srcPath;
        return this;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public Parser setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
        return this;
    }

    public abstract List<ClassDefinition> parse() throws Exception;

}
