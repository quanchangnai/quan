package quan.data.field;

import quan.data.Data;
import quan.data.Protection;
import quan.data.Transaction;
import quan.data.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
@SuppressWarnings("unchecked")
public class BaseField<V> extends Protection implements Field {

    private V value;

    public BaseField(V value) {
        this.value = value;
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

    public void setValue(V value, Data<?> owner, int position) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setFieldLog(transaction, this, value, owner, position);
        } else if (Transaction.isOptional()) {
            this.value = value;
            _setDataUpdatedField(owner, position);
        } else {
            Validations.transactionError();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
