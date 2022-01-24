package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;
import quan.data.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class BoolField extends Loggable implements Field {

    private boolean value;

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (boolean) log;
    }

    public boolean getValue() {
        return getValue(Transaction.get());
    }

    public boolean getValue(Transaction transaction) {
        if (transaction != null) {
            Boolean log = (Boolean) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(boolean value, Data<?> root) {
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
