package quan.database;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class ListLog<V> extends FieldLog<V> {

    private int modCount;

    public ListLog(Field<V> field, V value) {
        super(field, value);
    }

    public int getModCount() {
        return modCount;
    }

    public void incModCount() {
        modCount++;
    }
}
