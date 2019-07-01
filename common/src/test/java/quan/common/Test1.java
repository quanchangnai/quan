package quan.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/28.
 */
public class Test1 {

    private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) throws Exception {
        try {
            test1();
        } catch (Exception e) {
            logger.error("====", e);
        }
    }

    public static void test1() {
        System.err.println("test1");
        throw new RuntimeException("test1");
    }


}
