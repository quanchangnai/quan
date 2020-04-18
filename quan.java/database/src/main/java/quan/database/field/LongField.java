package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;

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
    public void commit(Object log) {
        this.value = (long) log;
    }

    public long getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Long logValue = (Long) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLog(long value, Data root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
