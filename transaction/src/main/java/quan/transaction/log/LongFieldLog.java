package quan.transaction.log;

import quan.transaction.field.LongField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class LongFieldLog implements FieldLog {

    private LongField field;

    private long value;

    public LongFieldLog(LongField field, long value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public LongField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
