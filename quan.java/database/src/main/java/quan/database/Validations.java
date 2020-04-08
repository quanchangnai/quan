package quan.database;

import java.util.Objects;

/**
 * 校验工具类
 * Created by quanchangnai on 2019/6/22.
 */
public class Validations {

    public static void validateMapKey(Object key) {
        Objects.requireNonNull(key, "map的key不能为null");
    }

    public static void validateCollectionValue(Object value) {
        Objects.requireNonNull(value, "list或者set的元素不能为null");
        if (value instanceof Entity) {
            validateEntityRoot((Entity) value);
        }
    }

    public static void validateEntityRoot(Entity entity) {
        if (entity == null) {
            return;
        }
        Data root = entity._getLogRoot();
        if (root != null) {
            throw new IllegalStateException(entity.getClass().getSimpleName() + "已经受到了" + root.getClass().getSimpleName() + "管理");
        }
    }

    public static void validateFieldValue(Object value) {
        Objects.requireNonNull(value, "字段值不能为null");
    }

}
