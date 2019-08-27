package quan.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        test1();
//        test2();

        String str = ":a::";
        String[] split =str.split(":",-1);
        System.err.println(Arrays.toString(split));
    }

    private static void test1() {

        Map<Integer, String> map = new HashMap<>();

        while (true) {
            String str = "role." + RandomStringUtils.randomAlphabetic(10);
            int hash = str.hashCode();

            String old = map.get(hash);
            if (old != null) {
                System.err.println("冲突:" + old + "," + str);
                break;
            }
            map.put(hash, str);
        }


    }

    private static void test2() {
        String pattern = "^[a-z][a-zA-Z\\d]*";
        String str = "type1";
        System.err.println(Pattern.matches(pattern, str));
    }

}
