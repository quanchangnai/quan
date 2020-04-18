package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.database.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/20.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class MapField<K, V> extends Node implements Map<K, V>, Field {

    private PMap<K, V> map = Empty.map();

    public MapField(Data root) {
        _setRoot(root);
    }

    public PMap<K, V> getValue() {
        return map;
    }

    @Override
    public void commit(Object log) {
        this.map = (PMap<K, V>) log;
    }

    @Override
    public void _setChildrenLogRoot(Data root) {
        for (V value : getLog().values()) {
            if (value instanceof Entity) {
                _setLogRoot((Entity) value, root);
            }
        }
    }

    private PMap<K, V> getLog(Transaction transaction) {
        PMap<K, V> log = (PMap<K, V>) _getFieldLog(transaction, this);
        if (log != null) {
            return log;
        }
        return map;
    }

    private PMap<K, V> getLog() {
        return getLog(Transaction.get(false));
    }

    @Override
    public int size() {
        return getLog().size();
    }

    @Override
    public boolean isEmpty() {
        return getLog().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getLog().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getLog().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getLog().get(key);
    }

    @Override
    public V put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        Transaction transaction = Transaction.get(true);
        PMap<K, V> oldMap = getLog(transaction);
        V oldValue = oldMap.get(key);
        PMap<K, V> newMap = oldMap.plus(key, value);

        Data root = _getLogRoot(transaction);

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
        PMap<K, V> oldMap = getLog(transaction);

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
        PMap<K, V> oldMap = getLog(transaction);
        Data root = _getLogRoot(transaction);

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
        if (getLog(transaction).isEmpty()) {
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
                        private Iterator<Entry<K, V>> it = getLog().entrySet().iterator();

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
        return String.valueOf(getLog());
    }
}
