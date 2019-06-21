package quan.database.field;

import quan.database.Transaction;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public interface Field {

    default Transaction checkTransaction() {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        return transaction;
    }

}
