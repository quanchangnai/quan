package quan.database;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public interface Field<V> {

    V getValue();

    void setValue(V value);

}
