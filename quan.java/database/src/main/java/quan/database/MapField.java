package quan.database;

import org.pcollections.Empty;
import org.pcollections.PMap;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/20.
 */
@SuppressWarnings({"unchecked"})
public final class MapField<K, V> extends Node implements Map<K, V>, Field<PMap<K, V>> {

    private PMap<K, V> map = Empty.map();

    public MapField(Data root) {
        _setRoot(root);
    }

    public void _setChildrenLogRoot(Data root) {
        for (V value : getLogValue().values()) {
            if (value instanceof Entity) {
                ((Entity) value)._setLogRoot(root);
            }
        }
    }

    public void setValue(PMap<K, V> map) {
        this.map = map;
    }

    @Override
    public PMap<K, V> getValue() {
        return map;
    }

    @Override
    public PMap<K, V> getLogValue() {
        FieldLog<PMap<K, V>> log = getLog(false);
        if (log != null) {
            return log.getValue();
        }
        return map;
    }

    @Override
    public int size() {
        return getLogValue().size();
    }

    @Override
    public boolean isEmpty() {
        return getLogValue().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getLogValue().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getLogValue().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getLogValue().get(key);
    }

    private FieldLog<PMap<K, V>> getLog(boolean add) {
        Transaction transaction = Transaction.get(add);
        if (transaction == null) {
            return null;
        }

        FieldLog<PMap<K, V>> log = (FieldLog<PMap<K, V>>) transaction.getFieldLog(this);
        if (add && log == null) {
            log = new FieldLog<>(this, map);
            transaction.addFieldLog(log);
            transaction.addDataLog(_getRoot());
        }

        return log;
    }

    @Override
    public V put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        FieldLog<PMap<K, V>> log = getLog(true);

        V oldValue = log.getValue().get(key);
        log.setValue(log.getValue().plus(key, value));

        if (value instanceof Entity) {
            ((Entity) value)._setLogRoot(_getRoot());
        }

        if (oldValue instanceof Entity) {
            ((Entity) oldValue)._setLogRoot(null);
        }

        return oldValue;
    }

    public V _put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        V oldValue = map.get(key);
        map = map.plus(key, value);

        if (value instanceof Entity) {
            ((Entity) value)._setRoot(_getRoot());
        }

        if (oldValue instanceof Entity) {
            ((Entity) oldValue)._setRoot(null);
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        FieldLog<PMap<K, V>> log = getLog(true);

        V value = log.getValue().get(key);
        log.setValue(log.getValue().minus(key));

        if (value instanceof Entity) {
            ((Entity) value)._setLogRoot(null);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            Validations.validateMapKey(key);
        }

        for (V value : m.values()) {
            Validations.validateCollectionValue(value);
        }

        FieldLog<PMap<K, V>> log = getLog(true);
        PMap<K, V> oldMap = log.getValue();
        log.setValue(oldMap.plusAll(m));

        for (K key : m.keySet()) {
            V newValue = m.get(key);
            if (newValue instanceof Entity) {
                ((Entity) newValue)._setLogRoot(_getRoot());
            }
            V oldValue = oldMap.get(key);
            if (oldValue != newValue && oldValue instanceof Entity) {
                ((Entity) oldValue)._setLogRoot(null);
            }
        }
    }

    @Override
    public void clear() {
        FieldLog<PMap<K, V>> log = getLog(true);
        if (log.getValue().isEmpty()) {
            return;
        }
        _setChildrenLogRoot(null);
        log.setValue(Empty.map());
    }

    private Set<K> keySet;

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new AbstractSet<K>() {
                @Override
                public int size() {
                    return MapField.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return MapField.this.isEmpty();
                }

                @Override
                public void clear() {
                    MapField.this.clear();
                }

                @Override
                public boolean contains(Object k) {
                    return MapField.this.containsKey(k);
                }

                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        //entrySet iterator
                        private Iterator<Entry<K, V>> it = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public K next() {
                            return it.next().getKey();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }
            };
        }

        return keySet;
    }

    private Collection<V> values;

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<V>() {
                @Override
                public int size() {
                    return MapField.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return MapField.this.isEmpty();
                }

                @Override
                public void clear() {
                    MapField.this.clear();
                }

                @Override
                public boolean contains(Object v) {
                    return MapField.this.containsValue(v);
                }

                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        //entrySet iterator
                        private Iterator<Entry<K, V>> it = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public V next() {
                            return it.next().getValue();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }
            };
        }
        return values;
    }

    private Set<Entry<K, V>> entrySet;

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null)
            entrySet = new AbstractSet<Entry<K, V>>() {
                @Override
                public int size() {
                    return MapField.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return MapField.this.isEmpty();
                }

                @Override
                public void clear() {
                    MapField.this.clear();
                }

                @Override
                public boolean contains(final Object e) {
                    if (!(e instanceof Entry)) {
                        return false;
                    }
                    V value = get(((Entry<?, ?>) e).getKey());
                    return value != null && value.equals(((Entry<?, ?>) e).getValue());
                }

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return new Iterator<Entry<K, V>>() {
                        //entrySet iterator
                        private Iterator<Entry<K, V>> it = getLogValue().entrySet().iterator();

                        private Entry<K, V> current;

                        @Override
                        public void remove() {
                            if (current == null) {
                                throw new IllegalStateException();
                            }
                            MapField.this.remove(current.getKey());
                        }

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Entry<K, V> next() {
                            return current = it.next();
                        }

                    };
                }

            };
        return entrySet;
    }


    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }
}
