package quan.database.field;

import quan.database.Data;
import quan.database.Loggable;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class DoubleField extends Loggable implements Field {

    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (double) logValue;
    }

    public double getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Double logValue = (Double) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(double value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }

}
