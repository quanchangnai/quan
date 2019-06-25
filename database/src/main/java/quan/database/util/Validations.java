package quan.database.util;

import quan.database.Bean;
import quan.database.Data;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class Validations {

    public static void validMapKey(Object key) {
        Objects.requireNonNull(key);
    }

    public static void validCollectionValue(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Bean) {
            validBeanRoot((Bean) value);
        }
    }

    public static void validBeanRoot(Bean bean) {
        if (bean == null) {
            return;
        }
        Data root = bean.getRoot();
        if (root != null) {
            throw new IllegalArgumentException("非法参数，当前已经受到了" + root.getClass().getSimpleName() + "管理");
        }
    }

    public static void validFieldValue(Object value) {
        Objects.requireNonNull(value);
    }

}
