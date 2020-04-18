package quan.database.field;

import quan.database.Data;
import quan.database.Loggable;
import quan.database.Transaction;

/**
 * Created by quanchangnai on 2020/4/17.
 */
public class ShortField extends Loggable implements Field {

    private short value;

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    protected void commit(Object log) {
        this.value = (short) log;
    }

    public short getLog() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Short log = (Short) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return value;
    }

    public void setLog(short value, Data<?> root) {
        _setFieldLog(Transaction.get(true), this, value, root);
    }

}
