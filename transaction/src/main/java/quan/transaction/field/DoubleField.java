package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.DoubleLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class DoubleField implements Field {

    private double value;

    public DoubleField() {
    }

    public DoubleField(double value) {
        this.value = value;
    }

    public double getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            DoubleLog log = (DoubleLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setLogValue(double value, MappingData root) {
        checkTransaction();
        Transaction transaction = Transaction.current();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        DoubleLog log = (DoubleLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new DoubleLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
