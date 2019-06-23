package quan.database.log;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class FieldLog<V> {

    private Field<V> field;

    private V value;

    public FieldLog(Field<V> field) {
        this.field = field;
    }

    public FieldLog(Field field, V value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public Field<V> getField() {
        return field;
    }

    public void commit() {
        field.setValue(value);
    }

}
