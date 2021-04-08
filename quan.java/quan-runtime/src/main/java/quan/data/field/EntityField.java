package quan.data.field;

import quan.data.*;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class EntityField<V extends Entity> extends Loggable implements Field {

    private V value;

    public EntityField() {
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = (V) log;
    }

    public V getValue() {
        return getValue(Transaction.get());
    }

    public V getValue(Transaction transaction) {
        if (transaction != null) {
            V log = (V) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setValue(V value, Data<?> root) {
        Validations.validateEntityRoot(value);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            V log = (V) _getFieldLog(transaction, this);
            if (log != null) {
                _setRootLog(transaction, log, null);
            }
            _setRootLog(transaction, value, root);
        } else if (Transaction.isOptional()) {
            _setRoot(this.value, null);
            this.value = value;
            _setRoot(value, root);
        } else {
            Validations.transactionError();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
