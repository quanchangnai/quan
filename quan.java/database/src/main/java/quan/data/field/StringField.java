package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class StringField extends Loggable implements Field {

    private String value = "";

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (String) log;
    }

    public String getValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            String log = (String) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(String value, Data<?> root) {
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
        return getValue();
    }

}
