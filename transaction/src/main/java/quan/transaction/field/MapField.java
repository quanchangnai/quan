package quan.transaction.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.transaction.Transaction;
import quan.transaction.log.MapLog;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class MapField<K, V> implements Map<K, V>, Field {

    private PMap<K, V> map = Empty.map();

    public MapField<K, V> setMap(PMap<K, V> map) {
        this.map = map;
        return this;
    }

    public Map<K, V> getMap() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            MapLog<K, V> log = (MapLog<K, V>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getMap();
            }
        }
        return map;
    }

    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getMap().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getMap().get(key);
    }

    private MapLog<K, V> getOrAddLog() {
        Transaction transaction = Transaction.current();
        MapLog<K, V> log = (MapLog<K, V>) transaction.getFieldLog(this);
        if (log == null) {
            log = new MapLog<>(this);
            transaction.addFieldLog(log);
        }
        return log;
    }

    @Override
    public V put(K key, V value) {
        MapLog<K, V> log = getOrAddLog();
        V oldValue = log.getMap().get(key);
        log.setMap(log.getMap().plus(key, value));
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        MapLog<K, V> log = getOrAddLog();
        V value = log.getMap().get(key);
        log.setMap(log.getMap().minus(key));
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        MapLog<K, V> log = getOrAddLog();
        log.setMap(log.getMap().plusAll(m));
    }

    @Override
    public void clear() {
        MapLog<K, V> log = getOrAddLog();
        log.setMap(Empty.map());
    }

    @Override
    public Set<K> keySet() {
        return getMap().keySet();
    }

    @Override
    public Collection<V> values() {
        return getMap().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getMap().entrySet();
    }

    @Override
    public String toString() {
        return getMap().toString();
    }
}
