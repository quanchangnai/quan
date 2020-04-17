package quan.database.field;

import quan.database.*;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class EntityField<V extends Entity> extends Node implements Field {

    private V value;

    public EntityField(Data root) {
        _setRoot(root);
    }

    public V getValue() {
        return value;
    }

    public V getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            V log = (V) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (V) value;
    }

    public void setLogValue(V value) {
        Validations.validateEntityRoot(value);

        V oldValue = getLogValue();
        if (oldValue != null) {
            _setLogRoot(oldValue, null);
        }

        _addFieldLog(Transaction.get(true), this, value, _getLogRoot());

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
