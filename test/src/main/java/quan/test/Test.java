package quan.test;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) {
        String packageName = Test.class.getPackage().getName();
        String newPackageName = "";
        for (int i = 0; i < packageName.length(); i++) {
            String c = String.valueOf(packageName.charAt(i));
            if (i == 0 || packageName.charAt(i - 1) == '.') {
                c = c.toUpperCase();
            }
            newPackageName += c;
        }


        System.err.println(packageName);
        System.err.println(newPackageName);
    }

}
