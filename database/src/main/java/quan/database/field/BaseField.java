package quan.database.field;

import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.FieldLog;
import quan.database.util.Validations;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class BaseField<V> implements Field<V> {

    private V value;

    public BaseField() {
    }

    public BaseField(V value) {
        this.value = value;
    }

    public V getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            FieldLog<V> log = (FieldLog<V>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setLogValue(V value, Data root) {
        Validations.validFieldValue(value);

        Transaction transaction = Validations.validTransaction();
        if (root != null) {
            transaction.addDataLog(root);
        }

        FieldLog<V> log = (FieldLog<V>) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new FieldLog<>(this, value));
        }

    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
