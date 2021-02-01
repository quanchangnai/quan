package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class DoubleField extends Loggable implements Field {

    private double value;

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (double) log;
    }

    public double getValue() {
        return getValue(Transaction.get());
    }

    public double getValue(Transaction transaction) {
        if (transaction != null) {
            Double log = (Double) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(double value, Data<?> root) {
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
