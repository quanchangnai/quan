package quan.transaction.field;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.StringLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class StringField implements Field {

    private String value;

    public StringField() {
    }

    public StringField(String value) {
        this.value = value;
    }

    public String getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            StringLog log = (StringLog) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLogValue(String value, MappingData root) {
        checkTransaction();
        Transaction transaction = Transaction.current();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        StringLog log = (StringLog) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new StringLog(this, value));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
