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
    public void commit(Object log) {
        this.value = (Boolean) log;
    }

    public boolean getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Boolean log = (Boolean) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(boolean value, Data<?> root) {
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
