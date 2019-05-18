package quan.transaction.log;

import quan.transaction.field.BooleanField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BooleanLog implements FieldLog {

    private BooleanField field;

    private boolean value;

    public BooleanLog(BooleanField field, boolean value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public BooleanField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
