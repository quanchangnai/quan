package quan.data.field;

import quan.data.Data;
import quan.data.Loggable;
import quan.data.Transaction;
import quan.data.Validations;

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
    public void commit(Object logValue) {
        this.value = (String) logValue;
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

    public void setLogValue(String value, Data<?> root) {
        Validations.validateFieldValue(value);
        _setFieldLog(Transaction.check(), this, value, root);
    }

    @Override
    public String toString() {
        return getLogValue();
    }

}
