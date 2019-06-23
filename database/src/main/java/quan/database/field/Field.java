package quan.database.field;

import quan.database.Transaction;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public interface Field<V> {

    V getValue();

    void setValue(V value);

}
