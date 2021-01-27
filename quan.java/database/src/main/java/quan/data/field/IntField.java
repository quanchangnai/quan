package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class IntField extends Loggable implements Field {

    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (int) logValue;
    }

    public int getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Integer logValue = (Integer) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setValue(int value, Data<?> root) {
        _setFieldLog(Transaction.check(), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
