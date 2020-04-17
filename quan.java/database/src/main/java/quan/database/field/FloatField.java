package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class FloatField extends LogAccessor implements Field {

    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (float) value;
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

    public void setLogValue(float value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }

}
