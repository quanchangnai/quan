package quan.database;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public final class EntityField<V extends Entity> extends BaseField<V> {

    public EntityField() {
    }

    public EntityField(V value) {
        super(value);
    }

    public void setLogValue(V value, Data root) {
        Validations.validateBeanRoot(value);

        Transaction transaction = Transaction.get(true);
        if (root != null) {
            transaction.addVersionLog(root);
        }

        V oldValue = getValue();
        if (oldValue != null) {
            oldValue._setLogRoot(null);
        }

        FieldLog<V> log = (FieldLog<V>) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new FieldLog<>(this, value));
        }

        if (value != null) {
            value._setLogRoot(root);
        }

    }

}
