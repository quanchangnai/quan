package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class FloatField implements Field {

    private float value;

    public FloatField() {
    }

    public FloatField(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
