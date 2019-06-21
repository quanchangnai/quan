package quan.database.log;

import quan.database.field.LongField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class LongLog implements FieldLog {

    private LongField field;

    private long value;

    public LongLog(LongField field, long value) {
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
