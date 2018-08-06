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

    protected void setMappingData(MappingData mappingData) {
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
        boolean notContains = current.add(e);
        if (!removed.remove(e) && notContains) {
            added.add(e);
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
            iterator.remove();
            if (!added.remove(lastRet)) {
                removed.add(lastRet);
            }
        }
    }

    @Override
    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", added=" + added +
                ", removed=" + removed +
                '}';
    }

    public static void main(String[] args) {
        // TODO: 2017/6/2 测试代码
        SetWrapper<Integer> set = new SetWrapper<>();
        set.add(1);
        set.commit();
        System.err.println(set);
        System.err.println("========");
        set.remove(1);
        set.rollback();
        System.err.println(set);
        System.err.println("========");
        set.remove(4);
        set.add(1);
        System.err.println(set);
        set.rollback();
        System.err.println("========");
        System.err.println(set);

        set.remove(2);
        set.remove(new IntegerWrapper(3));
        set.add(3);
        set.add(4);
        set.add(5);
        set.commit();
        System.err.println(set);
        System.err.println("========");
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            if (i < 3) {
                iterator.remove();
            }
        }
        set.rollback();
        System.err.println(set);
    }
}
