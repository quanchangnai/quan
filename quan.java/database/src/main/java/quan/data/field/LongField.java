package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class LongField extends Loggable implements Field {

    private long value;

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (long) log;
    }

    public long getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Long log = (Long) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(long value, Data<?> root) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setFieldLog(transaction, this, value, root);
        } else if (Transaction.isOptional()) {
            this.value = value;
        } else {
            Transaction.error();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
