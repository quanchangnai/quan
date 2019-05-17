package quan.transaction.log;

import quan.transaction.field.IntField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class IntFieldLog implements FieldLog {

    private IntField field;

    private int value;

    public IntFieldLog(IntField field, int value) {
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
