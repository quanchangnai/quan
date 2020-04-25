package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class FloatField extends Loggable implements Field {

    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (float) logValue;
    }

    public float getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Float logValue = (Float) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(float value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }

}
