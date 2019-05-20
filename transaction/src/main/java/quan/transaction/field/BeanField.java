package quan.transaction.field;

import quan.transaction.BeanData;
import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.BeanLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BeanField<T extends BeanData> implements Field {

    private T value;

    public BeanField() {
    }

    public BeanField(T value) {
        this.value = value;
    }

    public T getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            BeanLog<T> log = (BeanLog<T>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setLogValue(T value, MappingData root) {
        if (value != null && value.getRoot() != null && value.getRoot() != root) {
            throw new UnsupportedOperationException("设置的" + value.getClass().getSimpleName() + "当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + root);
        }

        if (getValue() == value) {
            return;
        }

        Transaction transaction = Transaction.current();
        if (root != null) {
            transaction.addVersionLog(root);
        }
        BeanLog<T> log = (BeanLog<T>) transaction.getFieldLog(this);
        if (log != null) {
            log.setValue(value);
        } else {
            transaction.addFieldLog(new BeanLog<>(this, value));
        }

        if (value != null) {
            value.setLogRoot(root);
        }
        if (this.getValue() != null) {
            this.getValue().setLogRoot(null);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
