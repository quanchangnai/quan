package quan.test;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) {
        String s = "";
        System.err.println(s.length());
        String[] strings = s.split(";");
        System.err.println(strings.length);
        for (int i = 0; i < strings.length; i++) {
            System.err.println(i + ":" + strings[0]);
        }
    }


}
