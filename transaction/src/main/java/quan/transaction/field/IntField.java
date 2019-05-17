package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class IntField implements TypeField {

    private int value;

    public IntField() {
    }

    public IntField(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
