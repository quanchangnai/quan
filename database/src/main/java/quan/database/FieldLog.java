package quan.database;

/**
 * Created by quanchangnai on 2019/5/16.
 */
class FieldLog<V> {

    private Field<V> field;

    private V value;

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
