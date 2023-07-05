package quan.util;

/**
 * 数字工具
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    public static void validateRange(Number value, Number min, Number max, String name) {
        name = name == null ? "参数" : name;

        if (value == null || min == null || max == null) {
            throw new IllegalArgumentException(String.format("%s(%s,%s,%s)不能为空", name, value, min, max));
        }

        if (Double.compare(value.doubleValue(), min.doubleValue()) < 0 || Double.compare(value.doubleValue(), max.doubleValue()) > 0) {
            throw new IllegalArgumentException(String.format("%s(%s)不在范围(%s,%s)之中", name, value, min, max));
        }
    }

    public static void validateRange(Number value, Number min, Number max) {
        validateRange(value, min, max, null);
    }

    public static void validateMin(Number value, Number min, String name) {
        name = name == null ? "参数" : name;

        if (value == null || min == null) {
            throw new IllegalArgumentException(String.format("%s(%s,%s)不能为空", name, value, min));
        }

        if (Double.compare(value.doubleValue(), min.doubleValue()) < 0) {
            throw new IllegalArgumentException(String.format("%s(%s)不能小于%s", name, value, min));
        }
    }

    public static void validateMin(Number value, Number min) {
        validateMin(value, min, null);
    }

    public static void validateMax(Number value, Number max, String name) {
        name = name == null ? "参数" : name;

        if (value == null || max == null) {
            throw new IllegalArgumentException(String.format("%s(%s,%s)不能为空", name, value, max));
        }

        if (Double.compare(value.doubleValue(), max.doubleValue()) > 0) {
            throw new IllegalArgumentException(String.format("%s(%s)不能大于%s", name, value, max));
        }
    }

    public static void validateMax(Number value, Number max) {
        validateMax(value, max, null);
    }

}
