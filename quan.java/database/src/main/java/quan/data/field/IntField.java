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
    public void commit(Object log) {
        this.value = (int) log;
    }

    public int getValue() {
        return getValue(Transaction.get());
    }

    public int getValue(Transaction transaction) {
        if (transaction != null) {
            Integer log = (Integer) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(int value, Data<?> root) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setFieldLog(transaction, this, value, root);
        } else if (Transaction.isOptional()) {
            this.value = value;
        } else {
            Transaction.error();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
