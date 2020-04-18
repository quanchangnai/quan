package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class IntField extends LogAccessor implements Field {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (int) log;
    }

    public int getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Integer log = (Integer) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(int value, Data root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
