package quan.database;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class BaseField<V> implements Field<V> {

    private V value;

    protected BaseField() {
    }

    public BaseField(V value) {
        this.value = value;
    }

    public V getValue() {
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
            transaction.addVersionLog(root);
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
