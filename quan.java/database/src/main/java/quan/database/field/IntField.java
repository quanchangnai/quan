package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class IntField extends LogAccessor implements Field {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (int) value;
    }

    public int getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Integer logValue = (Integer) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(int value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }

}
