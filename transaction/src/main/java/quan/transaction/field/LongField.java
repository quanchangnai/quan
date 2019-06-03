package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.LongLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class LongField implements Field {

    private long value;

    public LongField() {
    }

    public LongField(long value) {
        this.value = value;
    }

    public long getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            LongLog log = (LongLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setLogValue(long value, MappingData root) {
        Transaction transaction = checkTransaction();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        LongLog log = (LongLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new LongLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
