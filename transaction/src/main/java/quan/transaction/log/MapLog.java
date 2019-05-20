package quan.transaction.log;

import org.pcollections.PMap;
import quan.transaction.field.MapField;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class MapLog<K, V> implements FieldLog {


    private MapField<K, V> field;

    private PMap<K, V> map;

    public MapLog(MapField<K, V> field) {
        this.field = field;
        this.map = (PMap<K, V>) field.getMap();
    }

    @Override
    public MapField<K, V> getField() {
        return field;
    }

    public PMap<K, V> getMap() {
        return map;
    }

    public MapLog<K, V> setMap(PMap<K, V> map) {
        this.map = map;
        return this;
    }

    @Override
    public void commit() {
        field.setMap(map);
    }


}
