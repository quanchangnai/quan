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
    public void setValue(Object value) {
        this.value = (String) value;
    }

    public String getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            String logValue = (String) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(String value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }


}
