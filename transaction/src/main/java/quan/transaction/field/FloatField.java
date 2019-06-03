package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.FloatLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class FloatField implements Field {

    private float value;

    public FloatField() {
    }

    public FloatField(float value) {
        this.value = value;
    }

    public float getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            FloatLog log = (FloatLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setLogValue(float value, MappingData root) {
        Transaction transaction = checkTransaction();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        FloatLog log = (FloatLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new FloatLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
