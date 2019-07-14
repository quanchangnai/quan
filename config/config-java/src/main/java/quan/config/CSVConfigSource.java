package quan.config;

import java.io.File;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class CSVConfigSource extends ConfigSource {

    public CSVConfigSource(File sourceFile) {
        super(sourceFile);
    }

    public CSVConfigSource(String sourceFile) {
        super(sourceFile);
    }


}
