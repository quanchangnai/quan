package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.database.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/20.
 */
@SuppressWarnings("unchecked")
public final class MapField<K, V> extends Node implements Map<K, V>, Field {

    private PMap<K, V> map = Empty.map();

    private Delegate<K, V> delegate = new Delegate<>(this);

    public MapField(Data<?> root) {
        _setRoot(root);
    }

    public PMap<K, V> getMap() {
        return map;
    }

    public Delegate<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object logMap) {
        this.map = (PMap<K, V>) logMap;
    }

    @Override
    public void _setChildrenLogRoot(Data<?> root) {
        for (V value : getLogMap().values()) {
            if (value instanceof Entity) {
                _setLogRoot((Entity) value, root);
            }
        }
    }

    private PMap<K, V> getLogMap(Transaction transaction) {
        PMap<K, V> logMap = (PMap<K, V>) _getFieldLog(transaction, this);
        if (logMap != null) {
            return logMap;
        }
        return map;
    }

    private PMap<K, V> getLogMap() {
        return getLogMap(Transaction.get(false));
    }

    @Override
    public int size() {
        return getLogMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getLogMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getLogMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getLogMap().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getLogMap().get(key);
    }

    @Override
    public V put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        Transaction transaction = Transaction.get(true);
        PMap<K, V> oldMap = getLogMap(transaction);
        V oldValue = oldMap.get(key);
        PMap<K, V> newMap = oldMap.plus(key, value);

        Data<?> root = _getLogRoot(transaction);

        if (value instanceof Entity) {
            _setLogRoot((Entity) value, root);
        }
        if (oldValue instanceof Entity) {
            _setLogRoot((Entity) oldValue, null);
        }

        _setFieldLog(transaction, this, newMap, root);

        return oldValue;
    }

    public V _put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        V oldValue = map.get(key);
        map = map.plus(key, value);

        if (value instanceof Entity) {
            _setRoot((Entity) value, _getRoot());
        }

        if (oldValue instanceof Entity) {
            _setRoot((Entity) oldValue, null);
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        Transaction transaction = Transaction.get(true);
        PMap<K, V> oldMap = getLogMap(transaction);

        V value = oldMap.get(key);
        _setFieldLog(transaction, this, oldMap.minus(key), _getLogRoot(transaction));

        if (value instanceof Entity) {
            _setLogRoot((Entity) value, null);
        }


        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.keySet().forEach(Validations::validateMapKey);
        m.values().forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.get(true);
        PMap<K, V> oldMap = getLogMap(transaction);
        Data<?> root = _getLogRoot(transaction);

        _setFieldLog(transaction, this, oldMap.plusAll(m), root);

        for (K key : m.keySet()) {
            V newValue = m.get(key);
            if (newValue instanceof Entity) {
                _setLogRoot((Entity) newValue, root);
            }
            V oldValue = oldMap.get(key);
            if (oldValue != newValue && oldValue instanceof Entity) {
                _setLogRoot((Entity) oldValue, null);
            }
        }
    }

    @Override
    public void clear() {
        Transaction transaction = Transaction.get(true);
        if (getLogMap(transaction).isEmpty()) {
            return;
        }

        _setChildrenLogRoot(null);
        _setFieldLog(transaction, this, Empty.map(), _getLogRoot(transaction));
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
                        private Iterator<Entry<K, V>> it = getLogMap().entrySet().iterator();

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
        return String.valueOf(getLogMap());
    }


    private static class Delegate<K, V> implements Map<K, V> {

        private MapField<K, V> field;

        public Delegate(MapField<K, V> field) {
            this.field = field;
        }

        @Override
        public int size() {
            return field.size();
        }

        @Override
        public boolean isEmpty() {
            return field.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return field.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return field.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return field.get(key);
        }

        @Override
        public V put(K key, V value) {
            return field.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return field.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            field.putAll(m);
        }

        @Override
        public void clear() {
            field.clear();
        }

        @Override
        public Set<K> keySet() {
            return field.keySet();
        }

        @Override
        public Collection<V> values() {
            return field.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return field.entrySet();
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }
}
