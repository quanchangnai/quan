package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.IntLog;
import quan.transaction.log.LongLog;
import quan.transaction.log.ShortLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ShortField implements Field {

    private short value;

    public ShortField() {
    }

    public ShortField(short value) {
        this.value = value;
    }

    public short getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            ShortLog log = (ShortLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public void setLogValue(short value, MappingData root) {
        Transaction transaction = Transaction.current();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        ShortLog log = (ShortLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new ShortLog(this, value));
        }
    }

}
