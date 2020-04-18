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
    protected void commit(Object log) {
        this.value = (Boolean) log;
    }

    public boolean getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Boolean log = (Boolean) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(boolean value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
