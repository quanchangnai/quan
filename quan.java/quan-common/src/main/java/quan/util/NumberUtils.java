package quan.util;

/**
 * 数字工具
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    public static void checkRange(Number value, Number min, Number max) {
        if (value == null || min == null || max == null) {
            throw new IllegalArgumentException(String.format("参数(%s,%s,%s)不能为空", value, min, max));
        }

        if (Double.compare(value.doubleValue(), min.doubleValue()) < 0 || Double.compare(value.doubleValue(), max.doubleValue()) > 0) {
            throw new IllegalArgumentException(String.format("参数%s不在范围(%s,%s)之中", value, min, max));
        }
    }

    public static void checkMin(Number value, Number min) {
        if (value == null || min == null) {
            throw new IllegalArgumentException(String.format("参数(%s,%s)不能为空", value, min));
        }

        if (Double.compare(value.doubleValue(), min.doubleValue()) < 0) {
            throw new IllegalArgumentException(String.format("参数%s不能小于%s", value, min));
        }
    }

    public static void checkMax(Number value, Number max) {
        if (value == null || max == null) {
            throw new IllegalArgumentException(String.format("参数(%s,%s)不能为空", value, max));
        }

        if (Double.compare(value.doubleValue(), max.doubleValue()) > 0) {
            throw new IllegalArgumentException(String.format("参数%s不能大于%s", value, max));
        }
    }

}
