package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class DoubleField extends LogAccessor implements Field {

    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (double) log;
    }

    public double getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Double log = (Double) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(double value, Data root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
