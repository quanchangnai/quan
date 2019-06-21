package quan.database.log;

import quan.database.field.IntField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class IntLog implements FieldLog {

    private IntField field;

    private int value;

    public IntLog(IntField field, int value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public IntField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
