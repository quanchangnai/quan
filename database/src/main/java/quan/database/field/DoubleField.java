package quan.database.field;

import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.DoubleLog;

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

    public void setLogValue(double value, Data root) {
        Transaction transaction = checkTransaction();
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
