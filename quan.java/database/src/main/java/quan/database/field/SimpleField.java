package quan.database.field;

import quan.database.Data;
import quan.database.log.FieldLog;
import quan.database.Transaction;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2019/6/22.
 */
@SuppressWarnings("unchecked")
public class SimpleField<V> implements Field<V> {

    private V value;

    public SimpleField(V value) {
        this.value = value;
    }

    @Override
    public V getValue() {
        return value;
    }

    public V getLogValue() {
        Transaction transaction = Transaction.get();
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
        Validations.validateFieldValue(value);

        Transaction transaction = Transaction.get(true);
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
        return String.valueOf(getLogValue());
    }

}
