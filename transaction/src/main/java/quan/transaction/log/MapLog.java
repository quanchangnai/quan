package quan.transaction.log;

import org.pcollections.PMap;
import quan.transaction.field.MapField;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class MapLog<K, V> implements FieldLog {

    private MapField<K, V> field;

    private PMap<K, V> data;

    public MapLog(MapField<K, V> field) {
        this.field = field;
        this.data = (PMap<K, V>) field.getData();
    }

    @Override
    public MapField<K, V> getField() {
        return field;
    }

    public PMap<K, V> getData() {
        return data;
    }

    public MapLog<K, V> setData(PMap<K, V> data) {
        this.data = data;
        return this;
    }

    @Override
    public void commit() {
        field.setData(data);
    }


}
