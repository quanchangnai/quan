package quan.database;

import java.util.Objects;

/**
 * 校验工具类
 * Created by quanchangnai on 2019/6/22.
 */
class Validations {

    public static void validateMapKey(Object key) {
        Objects.requireNonNull(key);
    }

    public static void validateCollectionValue(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Bean) {
            validateBeanRoot((Bean) value);
        }
    }

    public static void validateBeanRoot(Bean bean) {
        if (bean == null) {
            return;
        }
        Data root = bean.getRoot();
        if (root != null) {
            throw new IllegalStateException(bean.getClass().getSimpleName() + "已经受到了" + root.getClass().getSimpleName() + "管理");
        }
    }

    public static void validateFieldValue(Object value) {
        Objects.requireNonNull(value);
    }

}
