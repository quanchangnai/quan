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
    public void setValue(Object value) {
        this.value = (double) value;
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

    public void setLogValue(double value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }

}
