package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class BoolField extends Loggable implements Field {

    private boolean value;

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (Boolean) logValue;
    }

    public boolean getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Boolean logValue = (Boolean) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setValue(boolean value, Data<?> root) {
        _setFieldLog(Transaction.check(), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
