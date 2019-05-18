package quan.transaction.log;

import quan.transaction.field.DoubleField;
import quan.transaction.field.FloatField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class FloatLog implements FieldLog {

    private FloatField field;

    private float value;

    public FloatLog(FloatField field, float value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public FloatField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
