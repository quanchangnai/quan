package quan.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/28.
 */
public class Test1 {

    private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test1() {
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String substring1 = url.substring(0, url.indexOf("?"));
//        substring1.substring(0)
        System.err.println(substring1);
    }


}
