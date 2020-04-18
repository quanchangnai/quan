package quan.database.field;

import quan.database.Data;
import quan.database.Loggable;
import quan.database.Transaction;

import java.util.Objects;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class StringField extends Loggable implements Field {

    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void commit(Object log) {
        this.value = (String) log;
    }

    public String getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            String log = (String) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(String value, Data<?> root) {
        Objects.requireNonNull(value);
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
