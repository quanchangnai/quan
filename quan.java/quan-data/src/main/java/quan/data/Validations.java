package quan.data;

import java.util.Objects;

/**
 * 校验工具类
 */
public class Validations {

    public static void validateMapKey(Object key) {
        Objects.requireNonNull(key, "map的key不能为null");
    }

    public static void validateCollectionValue(Object value) {
        Objects.requireNonNull(value, "集合元素不能为null");
        if (value instanceof Bean) {
            validateEntityRoot((Bean) value);
        }
    }

    public static void validateEntityRoot(Bean bean) {
        if (bean == null) {
            return;
        }
        Data<?> root = bean._getLogRoot();
        if (root != null) {
            throw new IllegalStateException("参数已经受到了" + root.getClass().getSimpleName() + "[" + root.id() + "]的管理");
        }
    }

    public static void validateFieldValue(Object value) {
        Objects.requireNonNull(value, "字段值不能为null");
    }

    public static Transaction validateTransaction() {
        Transaction transaction = Transaction.get();
        if (transaction == null) {
            transactionError();
        }
        return transaction;
    }

    public static void transactionError() {
        throw new IllegalStateException("当前不在事务中");
    }

}
