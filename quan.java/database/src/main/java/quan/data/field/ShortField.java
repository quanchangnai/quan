package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class ShortField extends Loggable implements Field {

    private short value;

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void commit(Object logValue) {
        this.value = (short) logValue;
    }

    public short getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Short logValue = (Short) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setValue(short value, Data<?> root) {
        _setFieldLog(Transaction.check(), this, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
