package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class LongField extends LogAccessor implements Field{

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (long) value;
    }

    public long getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Long logValue = (Long) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(long value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }

}
