package quan.database.field;

import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.ByteLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ByteField implements Field {

    private byte value;

    public ByteField() {
    }

    public ByteField(byte value) {
        this.value = value;
    }

    public byte getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            ByteLog log = (ByteLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public void setLogValue(byte value, Data root) {
        Transaction transaction = checkTransaction();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        ByteLog log = (ByteLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new ByteLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
