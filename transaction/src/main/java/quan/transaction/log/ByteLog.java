package quan.transaction.log;

import quan.transaction.field.ByteField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ByteLog implements FieldLog {

    private ByteField field;

    private byte value;

    public ByteLog(ByteField field, byte value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public ByteField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
