package quan.database.field;

import quan.database.Bean;
import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.FieldLog;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public final class BeanField<V extends Bean> extends BaseField<V> {

    public BeanField() {
    }

    public BeanField(V value) {
        super(value);
    }

    public void setLogValue(V value, Data root) {
        Validations.validBeanRoot(value);

        Transaction transaction = Transaction.get();
        if (root != null) {
            transaction.addVersionLog(root);
        }

        V oldValue = getValue();
        if (oldValue != null) {
            oldValue.setLogRoot(null);
        }

        FieldLog<V> log = (FieldLog<V>) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new FieldLog<>(this, value));
        }

        if (value != null) {
            value.setLogRoot(root);
        }

    }

}
