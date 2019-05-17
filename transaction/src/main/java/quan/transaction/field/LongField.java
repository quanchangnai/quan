package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class LongField implements Field {

    private long value;

    public LongField() {
    }

    public LongField(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
