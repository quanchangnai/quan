package quan.util;

/**
 * @author quanchangnai
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 驼峰字符串转成下划线分割的字符串
     *
     * @param s         源字符串
     * @param lowerCase 返回小写还是大写字符串
     */
    public static String toUnderscore(String s, boolean lowerCase) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i > 0 && c >= 'A' && c <= 'Z') {
                sb.append('_');
            }
            if (lowerCase) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }

        return sb.toString();
    }


}
