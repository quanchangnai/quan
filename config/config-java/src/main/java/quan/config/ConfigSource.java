package quan.config;

import java.io.File;

/**
 * 配置源
 * Created by quanchangnai on 2019/7/11.
 */
public abstract class ConfigSource {

    protected File sourceFile;

    public ConfigSource(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public ConfigSource(String sourceFile) {
        this.sourceFile = new File(sourceFile);
    }


}
