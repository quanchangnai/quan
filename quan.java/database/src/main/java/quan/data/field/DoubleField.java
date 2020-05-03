package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

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
        _setFieldLog(Transaction.check(), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }

}
