package quan.common.util;

import java.io.File;

/**
 * Created by quanchangnai on 2019/8/4.
 */
public class PathUtils {

    public static String crossPlatPath(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

}
