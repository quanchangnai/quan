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

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public void commit(Object log) {
        this.value = ((Log<V>) log).value;
    }

    public V getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<V> log = (Log<V>) _getFieldLog(transaction, this);
            if (log != null) {
                return log.value;
            }
        }
        return value;
    }

    public void setLogValue(V value, Data<?> root) {
        Validations.validateEntityRoot(value);

        Transaction transaction = Transaction.check();
        Log<V> log = (Log<V>) _getFieldLog(transaction, this);

        if (log == null) {
            log = new Log<>(this.value);
            _setFieldLog(transaction, this, log, root);
        }

        _setRootLog(transaction, log.value, null);
        log.value = value;
        _setRootLog(transaction, value, root);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }

    private static class Log<V> {

        V value;

        public Log(V value) {
            this.value = value;
        }

    }

}
