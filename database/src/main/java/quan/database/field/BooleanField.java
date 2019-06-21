package quan.database.field;

import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.BooleanLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BooleanField implements Field {

    private boolean value;

    public BooleanField() {
    }

    public BooleanField(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            BooleanLog log = (BooleanLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void setLogValue(boolean value, Data root) {
        Transaction transaction = checkTransaction();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        BooleanLog log = (BooleanLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new BooleanLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
