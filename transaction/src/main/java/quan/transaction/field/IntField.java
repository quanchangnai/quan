package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.IntLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class IntField implements Field {

    private int value;

    public IntField() {
    }

    public IntField(int value) {
        this.value = value;
    }

    public int getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            IntLog log = (IntLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setLogValue(int value, MappingData root) {
        Transaction transaction = checkTransaction();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        IntLog log = (IntLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new IntLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
