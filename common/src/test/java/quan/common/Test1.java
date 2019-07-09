package quan.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/6/28.
 */
public class Test1 {

    private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test1() {
        String s1 = "1312:22;1312:433\\23";
        String[] split = s1.split("\\:|\\;");
        System.err.println(Arrays.toString(split));
    }


}
