package quan.database;

import com.sleepycat.je.Transaction;

/**
 * Created by quanchangnai on 2019/6/22.
 */
@SuppressWarnings("unchecked")
public class BaseField<V> implements Field<V> {

    private V value;

    protected BaseField() {
    }

    public BaseField(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setLogValue(V value, Data root) {
        Validations.validateFieldValue(value);

    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
