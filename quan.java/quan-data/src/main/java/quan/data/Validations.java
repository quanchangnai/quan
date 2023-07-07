package quan.data;

import java.util.Objects;

/**
 * 校验工具类
 */
public class Validations {

    public static void validateMapKey(Object key) {
        Objects.requireNonNull(key, "map的key不能为空");
    }

    public static void validateCollectionValue(Object value) {
        Objects.requireNonNull(value, "集合元素不能为空");
        if (value instanceof Bean) {
            validateEntityOwner((Bean) value);
        }
    }

    public static void validateEntityOwner(Bean bean) {
        if (bean == null) {
            return;
        }

        Data<?> owner = bean._getLogOwner();
        if (owner != null) {
            throw new IllegalStateException(String.format("参数已经受到了[%s(%s)]的管理", owner.getClass().getName(), owner.id()));
        }
    }

    public static void validateFieldValue(Object value) {
        Objects.requireNonNull(value, "字段值不能为空");
    }

    public static void transactionError() {
        throw new IllegalStateException("当前不在事务中");
    }

}
