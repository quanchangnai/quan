package quan.database.field;

import quan.database.*;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class EntityField<V extends Entity> extends Loggable implements Field {

    private V value;

    public EntityField() {
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    protected void commit(Object log) {
        this.value = (V) log;
    }

    public V getLog() {
        Transaction transaction = Transaction.get(false);
        if (transaction != null) {
            Log<V> log = (Log<V>) _getFieldLog(transaction, this);
            if (log != null) {
                return log.value;
            }
        }
        return value;
    }

    public void setLog(V value, Data<?> root) {
        Validations.validateEntityRoot(value);

        Transaction transaction = Transaction.get(true);
        Log<V> log = (Log<V>) _getFieldLog(transaction, this);

        if (log == null) {
            log = new Log<>(this.value);
            _setFieldLog(transaction, this, log, root);
        }

        if (log.value != null) {
            _setNodeLog(transaction, log.value, null);
        }

        if (value != null) {
            _setNodeLog(transaction, value, null);
        }
    }


    private static class Log<V> {

        V value;

        public Log(V value) {
            this.value = value;
        }

    }

}
