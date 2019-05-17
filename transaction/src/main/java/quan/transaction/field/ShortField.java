package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class ShortField implements Field {

    private short value;

    public ShortField() {
    }

    public ShortField(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }
}
