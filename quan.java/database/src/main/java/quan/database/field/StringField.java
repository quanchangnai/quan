package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class StringField extends LogAccessor implements Field {

    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
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

    public void setLog(String value, Data root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }
    
}
