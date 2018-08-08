package quan.mongo;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class SetWrapper<E> extends AbstractSet<E> implements CollectionWrapper {

    //当前数据
    private Set<E> current = new HashSet<>();

    //记录添加的数据
    private Set<E> added = new HashSet<>();

    //记录删除的数据
    private Set<E> removed = new HashSet<>();

    /**
     * 当前拥有者
     */
    private MappingData currentOwner;

    /**
     * 原始拥有者
     */
    private MappingData originOwner;

    public SetWrapper(MappingData owner) {
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
        for (E e : current) {
            if (e instanceof ReferenceData) {
                ((ReferenceData) e).commit();
            }
        }
    }

    public void rollback() {
        currentOwner = originOwner;
        current.removeAll(added);
        current.addAll(removed);
        added.clear();
        removed.clear();
        for (E e : current) {
            if (e instanceof ReferenceData) {
                ((ReferenceData) e).rollback();
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
        onUpdateData(e);
        boolean notContains = current.add(e);
        if (!removed.remove(e) && notContains) {
            added.add(e);
        }
        if (e instanceof ReferenceData) {
            ((ReferenceData) e).setOwner(getOwner());
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
            onUpdateData(null);
            iterator.remove();
            if (!added.remove(lastRet)) {
                removed.add(lastRet);
                if (lastRet instanceof ReferenceData) {
                    ((ReferenceData) lastRet).setOwner(null);
                }
            }
        }
    }

    @Override
    public String toString() {
        return current.toString();
    }

    public String toDebugString() {
        return "{" +
                "current=" + toDebugString(current) +
                ", added=" + toDebugString(added) +
                ", removed=" + toDebugString(removed) +
                ", owner=" + getOwner() +
                '}';
    }

    public String toDebugString(Set<E> set) {
        String str = "[";
        for (E e : set) {
            if (e instanceof ReferenceData) {
                str += "" + ((ReferenceData) e).toDebugString() + ", ";
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
