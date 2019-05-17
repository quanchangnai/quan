package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ByteField implements Field {

    private byte value;

    public ByteField() {
    }

    public ByteField(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

}
