package quan.transaction.log;

import quan.transaction.field.DoubleField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class DoubleLog implements FieldLog {

    private DoubleField field;

    private double value;

    public DoubleLog(DoubleField field, double value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public DoubleField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
