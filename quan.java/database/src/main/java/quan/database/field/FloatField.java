package quan.database.field;

import quan.database.Data;
import quan.database.Loggable;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class FloatField extends Loggable implements Field {

    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    protected void commit(Object log) {
        this.value = (float) log;
    }

    public float getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Float log = (Float) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(float value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
