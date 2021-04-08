package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;
import quan.data.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class ShortField extends Loggable implements Field {

    private short value;

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (short) log;
    }

    public short getValue() {
        return getValue(Transaction.get());
    }

    public short getValue(Transaction transaction) {
        if (transaction != null) {
            Short log = (Short) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(short value, Data<?> root) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setFieldLog(transaction, this, value, root);
        } else if (Transaction.isOptional()) {
            this.value = value;
        } else {
            Validations.transactionError();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
