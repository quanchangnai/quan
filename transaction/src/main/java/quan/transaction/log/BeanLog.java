package quan.transaction.log;

import quan.transaction.MappingData;
import quan.transaction.BeanData;
import quan.transaction.field.BeanField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BeanLog<T extends BeanData> implements FieldLog {


    private BeanField<T> field;

    private T value;

    public BeanLog(BeanField<T> field, T value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public BeanField<T> getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
