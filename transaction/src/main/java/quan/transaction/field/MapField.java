package quan.transaction.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.transaction.BeanData;
import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.MapLog;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class MapField<K, V> extends BeanData implements Map<K, V>, Field {

    private PMap<K, V> data = Empty.map();

    public MapField(MappingData root) {
        setRoot(root);
    }

    protected void setChildrenLogRoot(MappingData root) {
        for (V value : getData().values()) {
            if (value instanceof BeanData) {
                ((BeanData) value).setLogRoot(root);
            }
        }
    }

    public MapField<K, V> setData(PMap<K, V> data) {
        this.data = data;
        return this;
    }

    public Map<K, V> getData() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            MapLog<K, V> log = (MapLog<K, V>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getData();
            }
        }
        return data;
    }

    @Override
    public int size() {
        return getData().size();
    }

    @Override
    public boolean isEmpty() {
        return getData().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getData().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getData().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getData().get(key);
    }

    private MapLog<K, V> getOrAddLog() {
        Transaction transaction = Transaction.current();
        if (getRoot() != null) {
            transaction.addVersionLog(getRoot());
        }
        MapLog<K, V> log = (MapLog<K, V>) transaction.getFieldLog(this);
        if (log == null) {
            log = new MapLog<>(this);
            transaction.addFieldLog(log);
        }
        return log;
    }

    private void validKey(K key) {
        List<Class<?>> allowedClasses = Arrays.asList(Byte.class, Boolean.class, Short.class, Integer.class, Long.class, Double.class, String.class);
        if (!allowedClasses.contains(key.getClass())) {
            throw new IllegalArgumentException("不允许该类型作为Key:" + key.getClass());
        }
    }


    @Override
    public V put(K key, V value) {
        validKey(key);
        validValue(value);

        MapLog<K, V> log = getOrAddLog();

        V oldValue = log.getData().get(key);
        log.setData(log.getData().plus(key, value));

        if (value instanceof BeanData) {
            ((BeanData) value).setLogRoot(getRoot());
        }
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        MapLog<K, V> log = getOrAddLog();
        V value = log.getData().get(key);
        log.setData(log.getData().minus(key));
        if (value instanceof BeanData) {
            ((BeanData) value).setLogRoot(null);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            validKey(key);
        }
        for (V value : m.values()) {
            validValue(value);
        }
        MapLog<K, V> log = getOrAddLog();
        log.setData(log.getData().plusAll(m));
        setChildrenLogRoot(getRoot());
    }

    @Override
    public void clear() {
        MapLog<K, V> log = getOrAddLog();
        if (log.getData().isEmpty()) {
            return;
        }
        setChildrenLogRoot(null);
        log.setData(Empty.map());
    }

    @Override
    public Set<K> keySet() {
        return getData().keySet();
    }

    @Override
    public Collection<V> values() {
        return getData().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getData().entrySet();
    }

    @Override
    public String toString() {
        return getData().toString();
    }
}
