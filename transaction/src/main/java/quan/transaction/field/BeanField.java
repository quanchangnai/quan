package quan.transaction.field;

import quan.transaction.BeanData;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BeanField<T extends BeanData> implements Field {

    private T value;

    public BeanField() {
    }

    public BeanField(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
