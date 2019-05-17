package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BooleanField implements Field {

    private boolean value;

    public BooleanField() {
    }

    public BooleanField(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

}
