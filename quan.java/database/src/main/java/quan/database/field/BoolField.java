package quan.database.field;

import quan.database.Data;
import quan.database.Loggable;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class BoolField extends Loggable implements Field {

    private boolean value;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (Boolean) logValue;
    }

    public boolean getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Boolean logValue = (Boolean) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(boolean value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
