package quan.test;

import java.io.File;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) {
        String path = "aa/bb/vv\\cc\\dd";
        path = path.replace("/", File.separator).replace("\\", File.separator);
        System.err.println(path);
    }


}
