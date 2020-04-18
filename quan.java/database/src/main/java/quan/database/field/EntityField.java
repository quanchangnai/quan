package quan.database.field;

import quan.database.*;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class EntityField<V extends Entity> extends Node implements Field {

    private V value;

    public EntityField(Data root) {
        _setRoot(root);
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (V) log;
    }

    public V getLog() {
        return getLog(Transaction.get());
    }

    private V getLog(Transaction transaction) {
        if (transaction != null) {
            V log = (V) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }


    public void setLog(V value) {
        Validations.validateEntityRoot(value);

        Transaction transaction = Transaction.get(true);
        V log = getLog(transaction);
        Data root = _getLogRoot(transaction);

        if (log != null) {
            _setLogRoot(log, null);
        }
        if (value != null) {
            _setLogRoot(value, root);
        }

        _setFieldLog(transaction, this, value, root);
    }

    @Override
    protected void _setChildrenLogRoot(Data root) {
        if (value != null) {
            _setLogRoot(value, root);
        }
    }

}
