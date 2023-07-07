package quan.data.field;

import quan.data.Bean;
import quan.data.Data;
import quan.data.Transaction;
import quan.data.Validations;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class BeanField<V extends Bean> extends BaseField<V> implements Field {

    public BeanField() {
        super(null);
    }

    public void setValue(V value, Data<?> owner, int position) {
        Validations.validateEntityOwner(value);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            V log = (V) _getFieldLog(transaction, this);
            if (log != null) {
                _setNodeLog(transaction, log, null, 0);
            }
            _setFieldLog(transaction, this, value, owner, position);
        } else if (Transaction.isOptional()) {
            _setNodeOwner(this.getValue(), null, 0);
            setValue(value);
            _setNodeOwner(value, owner, position);
        } else {
            Validations.transactionError();
        }
    }

}
