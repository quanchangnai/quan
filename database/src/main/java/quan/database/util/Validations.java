package quan.database.util;

import quan.database.Bean;
import quan.database.Data;
import quan.database.Transaction;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class Validations {

    public static Transaction validTransaction() {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        return transaction;
    }

    public static void validMapKey(Object key) {
        Objects.requireNonNull(key);
    }

    public static void validCollectionValue(Object value) {
        Objects.requireNonNull(value);

        if (value instanceof Bean) {
            Data valueRoot = ((Bean) value).getRoot();
            if (valueRoot != null) {
                throw new IllegalArgumentException("添加的" + value.getClass().getSimpleName() + "当前正受到" + Data.class.getSimpleName() + "管理:" + valueRoot);
            }
        }

    }

    public static void validFieldValue(Object value) {
        if (value instanceof Bean) {
            Bean bean = (Bean) value;
            if (bean != null && bean.getRoot() != null) {
                throw new UnsupportedOperationException("设置的" + value.getClass().getSimpleName() + "当前正受到其它" + Data.class.getSimpleName() + "管理:" + bean.getRoot());
            }
        } else {
            Objects.requireNonNull(value);
        }

    }
}
