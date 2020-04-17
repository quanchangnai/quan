package quan.database.field;

import quan.database.Data;
import quan.database.LogAccessor;
import quan.database.Transaction;
import quan.database.Validations;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class ShortField extends LogAccessor implements Field {

    private short value;

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (short) value;
    }

    public short getLogValue() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Short logValue = (Short) _getFieldLog(transaction, this);
            if (logValue != null) {
                return logValue;
            }
        }
        return value;
    }

    public void setLogValue(short value, Data root) {
        _addFieldLog(Transaction.get(true), this, value, root);
    }

}
