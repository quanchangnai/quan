package quan.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @author quanchangnai
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 驼峰字符串转成蛇形字符串(下划线分隔)
     *
     * @param s     源字符串
     * @param lower 返回小写还是大写字符串
     */
    public static String toSnakeCase(String s, boolean lower) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i > 0 && c >= 'A' && c <= 'Z') {
                sb.append('_');
            }
            if (lower) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }

        return sb.toString();
    }

    /**
     * 蛇形字符串(下划线分隔)转成驼峰字符串
     *
     * @param s     源字符串
     * @param lower 返回小驼峰还是大驼峰字符串
     */
    public static String toCamelCase(String s, boolean lower) {
        StringBuilder sb = new StringBuilder();

        boolean underscore = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                underscore = true;
            } else {
                if (!lower && sb.length() == 0 || underscore && sb.length() > 0) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                underscore = false;
            }
        }

        return sb.toString();
    }

    /**
     * 替换指定正则捕获组的字符串
     */
    public static String replaceStringGroup(String source, String replacement, Pattern pattern, int group) {
        Objects.requireNonNull(source, "原字符串不能为空");
        Objects.requireNonNull(pattern, "正则表达式不能为空");
        Objects.requireNonNull(replacement, "替换字符串不能为空");

        Matcher matcher = pattern.matcher(source);

        if (group < 0 || group > matcher.groupCount()) {
            throw new IllegalArgumentException("正则捕获组" + group + "错误");
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;

        while (matcher.find()) {
            sb.append(source, index, matcher.start(group));
            sb.append(replacement);
            index = matcher.end(group);
        }

        sb.append(source, index, source.length());

        return sb.toString();
    }

    /**
     * @see #replaceStringGroup(String, String, Pattern, int)
     */
    public static String replaceStringGroup(String source, String replacement, String pattern, int group) {
        Objects.requireNonNull(pattern, "正则表达式不能为空");
        return replaceStringGroup(source, replacement, Pattern.compile(pattern), group);
    }

}
