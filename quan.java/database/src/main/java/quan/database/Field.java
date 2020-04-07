package quan.database;

/**
 * 数据字段包装器
 * Created by quanchangnai on 2019/6/22.
 */
public interface Field<V> {

    V getValue();

    V getLogValue();

    void setValue(V value);

}
