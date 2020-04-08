package quan.database.field;

import quan.database.*;
import quan.database.log.FieldLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class EntityField<V extends Entity> extends Node implements Field<V> {

    private V value;

    public EntityField(Data root) {
        _setRoot(root);
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            FieldLog<V> log = (FieldLog<V>) _getFieldLog(transaction, this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }

    public void setLogValue(V value) {
        Validations.validateEntityRoot(value);

        Transaction transaction = Transaction.get(true);
        _addDataLog(transaction, _getLogRoot());

        V oldValue = getLogValue();
        if (oldValue != null) {
            _setLogRoot(oldValue, null);
        }

        FieldLog<V> log = (FieldLog<V>) _getFieldLog(transaction, this);
        if (log != null) {
            log.setValue(value);
        } else {
            _addFieldLog(transaction, new FieldLog<>(this, value));
        }

        if (value != null) {
            _setLogRoot(value, _getLogRoot());
        }

    }

    @Override
    protected void _setChildrenLogRoot(Data root) {
        if (value != null) {
            _setLogRoot(value, root);
        }
    }

}
