package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.database.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/20.
 */
@SuppressWarnings({"unchecked"})
public final class MapField<K, V> extends Node implements Map<K, V>, Field {

    private PMap<K, V> map = Empty.map();

    public MapField(Data root) {
        _setRoot(root);
    }

    public void _setChildrenLogRoot(Data root) {
        for (V value : getLogValue().values()) {
            if (value instanceof Entity) {
                _setLogRoot((Entity) value, _getLogRoot());
            }
        }
    }


    public PMap<K, V> getValue() {
        return map;
    }

    public void setValue(PMap<K, V> map) {
        this.map = map;
    }

    @Override
    public void setValue(Object map) {
        this.map = (PMap<K, V>) map;
    }

    public PMap<K, V> getLogValue() {
        PMap<K, V> log = (PMap<K, V>) _getFieldLog(Transaction.get(true), this);
        if (log != null) {
            return log;
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

    @Override
    public V put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        PMap<K, V> oldMap = getLogValue();
        V oldValue = oldMap.get(key);
        PMap<K, V> newMap = oldMap.plus(key, value);

        if (value instanceof Entity) {
            _setLogRoot((Entity) value, _getLogRoot());
        }

        if (oldValue instanceof Entity) {
            _setLogRoot((Entity) oldValue, null);
        }

        _addFieldLog(Transaction.get(true), this, newMap, null);

        return oldValue;
    }

    public V _put(K key, V value) {
        Validations.validateMapKey(key);
        Validations.validateCollectionValue(value);

        V oldValue = map.get(key);
        map = map.plus(key, value);

        if (value instanceof Entity) {
            _setLogRoot((Entity) value, _getLogRoot());
        }

        if (oldValue instanceof Entity) {
            _setLogRoot((Entity) oldValue, null);
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        Transaction transaction = Transaction.get(true);
        PMap<K, V> oldMap = getLogValue();

        V value = oldMap.get(key);
        _addFieldLog(transaction, this, oldMap.minus(key), null);

        if (value instanceof Entity) {
            _setLogRoot((Entity) value, null);
        }


        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.keySet().forEach(Validations::validateMapKey);
        m.values().forEach(Validations::validateCollectionValue);

        PMap<K, V> oldMap = getLogValue();
        _addFieldLog(Transaction.get(true), this, oldMap.plusAll(m), null);

        for (K key : m.keySet()) {
            V newValue = m.get(key);
            if (newValue instanceof Entity) {
                _setLogRoot((Entity) newValue, _getLogRoot());
            }
            V oldValue = oldMap.get(key);
            if (oldValue != newValue && oldValue instanceof Entity) {
                _setLogRoot((Entity) oldValue, null);
            }
        }
    }

    @Override
    public void clear() {
        if (getLogValue().isEmpty()) {
            return;
        }

        _setChildrenLogRoot(null);
        _addFieldLog(Transaction.get(true), this, Empty.map(), null);
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
