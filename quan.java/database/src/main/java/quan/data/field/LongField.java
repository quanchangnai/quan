package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class LongField extends Loggable implements Field {

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (long) logValue;
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

    public void setLogValue(long value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }

}
