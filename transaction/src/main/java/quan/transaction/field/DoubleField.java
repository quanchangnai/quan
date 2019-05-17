package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class DoubleField implements Field {

    private double value;

    public DoubleField() {
    }

    public DoubleField(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
