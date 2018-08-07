package quan.mongo;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set
 * Created by quanchangnai on 2017/5/23.
 */
public class SetWrapper<E> extends AbstractSet<E> implements Data, UpdateCallback {

    //当前数据
    private Set<E> current = new HashSet<>();

    //记录添加的数据
    private Set<E> added = new HashSet<>();

    //记录删除的数据
    private Set<E> removed = new HashSet<>();

    /**
     * 所属的MappingData
     */
    private MappingData mappingData;

    /**
     * 不要手动调用
     *
     * @param mappingData
     */
    @Override
    public void setMappingData(MappingData mappingData) {
        this.mappingData = mappingData;
    }

    @Override
    public MappingData getMappingData() {
        return mappingData;
    }

    @Override
    public void commit() {
        added.clear();
        removed.clear();
        for (E e : current) {
            if (e instanceof Data) {
                ((Data) e).commit();
            }
        }
    }

    @Override
    public void rollback() {
        current.removeAll(added);
        current.addAll(removed);
        added.clear();
        removed.clear();
        for (E e : current) {
            if (e instanceof Data) {
                ((Data) e).rollback();
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new InnerIterator();
    }

    @Override
    public int size() {
        return current.size();
    }


    @Override
    public boolean add(E e) {
        onUpdateData();
        boolean notContains = current.add(e);
        if (!removed.remove(e) && notContains) {
            added.add(e);
        }
        if (e instanceof UpdateCallback) {
            ((UpdateCallback) e).setMappingData(getMappingData());
        }
        return notContains;
    }

    @Override
    public boolean remove(Object o) {
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (e.equals(o)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    private class InnerIterator implements Iterator<E> {
        private Iterator<E> iterator = current.iterator();
        private E lastRet;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            return lastRet = iterator.next();
        }

        @Override
        public void remove() {
            onUpdateData();
            iterator.remove();
            if (!added.remove(lastRet)) {
                removed.add(lastRet);
            }
        }
    }

    @Override
    public String toString() {
        return current.toString();
    }

    @Override
    public String toDebugString() {
        return "{" +
                "current=" + toDebugString(current) +
                ", added=" + toDebugString(added) +
                ", removed=" + toDebugString(removed) +
                '}';
    }

    public String toDebugString(Set<E> set) {
        String str = "[";
        for (E e : set) {
            if (e instanceof Data) {
                str += "" + ((Data) e).toDebugString() + ", ";
            } else {
                str += "" + e + ", ";
            }
        }
        if (str.endsWith(", ")) {
            str = str.substring(0, str.length() - 2);
        }
        str += "]";
        return str;
    }

}
