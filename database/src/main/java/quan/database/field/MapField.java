package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.database.Bean;
import quan.database.Data;
import quan.database.Node;
import quan.database.Transaction;
import quan.database.log.FieldLog;
import quan.database.util.Validations;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public final class MapField<K, V> extends Node implements Map<K, V>, Field<PMap<K, V>> {

    private PMap<K, V> data = Empty.map();

    public MapField(Data root) {
        setRoot(root);
    }

    public void setChildrenLogRoot(Data root) {
        for (V value : getValue().values()) {
            if (value instanceof Bean) {
                ((Bean) value).setLogRoot(root);
            }
        }
    }

    public void setValue(PMap<K, V> data) {
        this.data = data;
    }

    @Override
    public PMap<K, V> getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            FieldLog<PMap<K, V>> log = (FieldLog<PMap<K, V>>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
        }
        return data;
    }

    @Override
    public int size() {
        return getValue().size();
    }

    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getValue().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getValue().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getValue().get(key);
    }

    private FieldLog<PMap<K, V>> getOrAddLog() {
        Transaction transaction = Transaction.get();

        Data root = getRoot();
        if (root != null) {
            transaction.addVersionLog(root);
        }

        FieldLog<PMap<K, V>> log = (FieldLog<PMap<K, V>>) transaction.getFieldLog(this);
        if (log == null) {
            log = new FieldLog<>(this, data);
            transaction.addFieldLog(log);
        }
        return log;
    }

    @Override
    public V put(K key, V value) {
        Validations.validMapKey(key);
        Validations.validCollectionValue(value);

        FieldLog<PMap<K, V>> log = getOrAddLog();

        V oldValue = log.getValue().get(key);
        log.setValue(log.getValue().plus(key, value));

        if (value instanceof Bean) {
            ((Bean) value).setLogRoot(getRoot());
        }

        if (oldValue instanceof Bean) {
            ((Bean) value).setLogRoot(null);
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        FieldLog<PMap<K, V>> log = getOrAddLog();

        V value = log.getValue().get(key);
        log.setValue(log.getValue().minus(key));

        if (value instanceof Bean) {
            ((Bean) value).setLogRoot(null);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            Validations.validMapKey(key);
        }

        for (V value : m.values()) {
            Validations.validCollectionValue(value);
        }

        FieldLog<PMap<K, V>> log = getOrAddLog();
        PMap<K, V> oldData = log.getValue();
        log.setValue(oldData.plusAll(m));

        for (K key : m.keySet()) {
            V newValue = m.get(key);
            if (newValue instanceof Bean) {
                ((Bean) newValue).setLogRoot(getRoot());
            }
            V oldValue = oldData.get(key);
            if (oldValue instanceof Bean) {
                ((Bean) oldValue).setLogRoot(null);
            }
        }
    }

    @Override
    public void clear() {
        FieldLog<PMap<K, V>> log = getOrAddLog();
        if (log.getValue().isEmpty()) {
            return;
        }
        setChildrenLogRoot(null);
        log.setValue(Empty.map());
    }

    @Override
    public Set<K> keySet() {
        return getValue().keySet();
    }

    @Override
    public Collection<V> values() {
        return getValue().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getValue().entrySet();
    }


    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}