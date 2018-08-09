package quan.mongo;

import java.util.*;
import java.util.function.Consumer;

/**
 * Map包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class MapWrapper<K, V> extends AbstractMap<K, V> implements Container {

    //当前数据
    private Map<K, V> current = new HashMap<>();

    //记录添加的数据
    private Set<K> added = new HashSet<>();

    //记录删除的数据
    private Map<K, V> removed = new HashMap<>();

    //记录被替换的数据
    private final Map<K, V> replaced = new HashMap<>();

    /**
     * 当前拥有者
     */
    private MappingData currentOwner;

    /**
     * 原始拥有者
     */
    private MappingData originOwner;

    public MapWrapper(MappingData owner) {
        this.currentOwner = owner;
        this.originOwner = owner;
    }

    /**
     * 不要手动调用
     *
     * @param owner
     */
    @Override
    public void setOwner(MappingData owner) {
        this.currentOwner = owner;
    }

    @Override
    public MappingData getOwner() {
        return currentOwner;
    }


    public void commit() {
        originOwner = currentOwner;
        added.clear();
        removed.clear();
        replaced.clear();
        for (V value : current.values()) {
            if (value instanceof ReferenceData) {
                ((ReferenceData) value).commit();
            }
        }
    }

    public void rollback() {
        currentOwner = originOwner;
        current.keySet().removeAll(added);
        current.putAll(removed);
        current.putAll(replaced);
        added.clear();
        removed.clear();
        replaced.clear();
        for (V value : current.values()) {
            if (value instanceof ReferenceData) {
                ((ReferenceData) value).rollback();
            }
        }
    }

    private void onPut(K key, V origin, V value) {
        Objects.requireNonNull(key, "key不能为null");
        if (!allowedClasses.contains(key.getClass())) {
            throw new IllegalArgumentException("key的类型非法：" + key.getClass());
        }
        checkUpdateData(true, value);

        if (this.added.contains(key)) {
            return; // 之前有添加数据的记录，不需要再次记录
        }

        V removedValue = this.removed.remove(key);
        if (null != removedValue) {
            //删除+添加=替换
            this.replaced.put(key, removedValue);
            return;
        }
        if (this.replaced.containsKey(key)) {
            return;// 之前有替换数据的记录，不需要再次记录
        }

        if (null == origin) {
            this.added.add(key);
        } else {
            this.replaced.put(key, origin);
        }
    }

    @Override
    public V put(K key, V value) {
        onPut(key, current.get(key), value);

        V origin = current.put(key, value);

        if (value instanceof ReferenceData) {
            ((ReferenceData) value).setOwner(getOwner());
        }
        if (origin instanceof ReferenceData) {
            ((ReferenceData) origin).setOwner(null);
        }
        return origin;
    }

    private void onRemove(K key, V value) {
        checkUpdateData(false, null);
        if (!this.added.remove(key)) {
            V replacedValue = this.replaced.remove(key);
            this.removed.put(key, replacedValue == null ? value : replacedValue);
        }
        if (value instanceof ReferenceData) {
            ((ReferenceData) value).setOwner(null);
        }
    }

    @Override
    public V remove(Object key) {
        V value = current.get(key);
        if (value != null) {
            onRemove((K) key, value);
        }
        current.remove(key);
        if (value instanceof ReferenceData) {
            ((ReferenceData) value).setOwner(null);
        }
        return value;
    }

    @Override
    public V get(Object key) {
        return current.get(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    private EntrySet entrySet;

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet != null ? entrySet : (entrySet = new EntrySet());
    }

    private KeySet keySet;

    public Set<K> keySet() {
        return keySet != null ? keySet : (keySet = new KeySet());
    }

    private Values values;

    @Override
    public Collection<V> values() {
        return values != null ? values : (values = new Values());
    }


    @Override
    public void clear() {
        for (Entry<K, V> entry : current.entrySet()) {
            onRemove(entry.getKey(), entry.getValue());
        }
        current.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return current.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return current.containsValue(value);
    }

    @Override
    public boolean equals(Object o) {
        return current.equals(o);
    }

    @Override
    public int hashCode() {
        return current.hashCode();
    }

    @Override
    public int size() {
        return current.size();
    }

    @Override
    public boolean isEmpty() {
        return current.isEmpty();
    }

    private abstract class InnerIterator<E> implements Iterator<E> {
        private Iterator<Entry<K, V>> it;
        private Entry<K, V> currentEntry;

        public InnerIterator() {
            this.it = MapWrapper.this.current.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public void remove() {
            onRemove(currentEntry.getKey(), currentEntry.getValue());
            it.remove();
        }

        protected Entry<K, V> nextEntry() {
            return currentEntry = it.next();
        }
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
        public int size() {
            return MapWrapper.this.size();
        }

        public void clear() {
            MapWrapper.this.clear();
        }

        public Iterator<Entry<K, V>> iterator() {
            return new InnerIterator<Entry<K, V>>() {
                @Override
                public Entry<K, V> next() {
                    return nextEntry();
                }
            };
        }

        public boolean contains(Object o) {
            return MapWrapper.this.current.entrySet().contains(o);
        }

        public boolean remove(Object o) {
            //这里得不到原来的数据，没法记录操作
            throw new UnsupportedOperationException();
        }

        public Spliterator<Entry<K, V>> spliterator() {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer<? super Entry<K, V>> action) {
            MapWrapper.this.current.entrySet().forEach(action);
        }
    }

    private class KeySet extends AbstractSet<K> {
        public int size() {
            return MapWrapper.this.size();
        }

        public void clear() {
            MapWrapper.this.clear();
        }

        public Iterator<K> iterator() {
            return new InnerIterator<K>() {
                @Override
                public K next() {
                    return nextEntry().getKey();
                }
            };
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object key) {
            return MapWrapper.this.remove(key) != null;
        }

        public Spliterator<K> spliterator() {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer<? super K> action) {
            current.keySet().forEach(action);
        }
    }

    private class Values extends AbstractCollection<V> {
        public int size() {
            return MapWrapper.this.size();
        }

        public void clear() {
            MapWrapper.this.clear();
        }

        public Iterator<V> iterator() {
            return new InnerIterator<V>() {
                @Override
                public V next() {
                    return nextEntry().getValue();
                }
            };
        }

        public boolean contains(Object o) {
            return MapWrapper.this.containsValue(o);
        }

        public Spliterator<V> spliterator() {
            throw new UnsupportedOperationException();
        }

        public final void forEach(Consumer<? super V> action) {
            MapWrapper.this.current.values().forEach(action);
        }
    }

    @Override
    public String toString() {
        return current.toString();
    }

    public String toDebugString() {
        return "{" +
                "current=" + toDebugString(current) +
                ", added=" + added +
                ", removed=" + toDebugString(removed) +
                ", replaced=" + toDebugString(replaced) +
                ", owner=" + getOwner() +
                '}';
    }

    public String toDebugString(Map<K, V> map) {
        String str = "[";
        for (K key : current.keySet()) {
            V value = current.get(key);
            str += "" + key;
            if (value instanceof ReferenceData) {
                str += "=" + ((ReferenceData) value).toDebugString() + ", ";
            } else {
                str += "=" + value + ", ";
            }
        }
        if (str.endsWith(", ")) {
            str = str.substring(0, str.length() - 2);
        }
        str += "]";
        return str;
    }
}
