package quan.transaction.log;

import quan.transaction.field.ShortField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ShortLog implements FieldLog {

    private ShortField field;

    private short value;

    public ShortLog(ShortField field, short value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    @Override
    public ShortField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
