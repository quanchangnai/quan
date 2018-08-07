package quan.mongo;

import java.util.*;

/**
 * List
 * Created by quanchangnai on 2017/5/23.
 */
public class ListWrapper<E> extends AbstractList<E> implements Data, UpdateCallback {

    //当前数据
    private List<E> current = new ArrayList<>();

    //记录所有(add,remove,set)操作
    private Deque<Operation<E>> operations = new ArrayDeque<>();

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
        operations.clear();
        for (E e : current) {
            if (e instanceof Data) {
                ((Data) e).commit();
            }
        }
    }

    @Override
    public void rollback() {
        while (!operations.isEmpty()) {
            Operation<E> operation = operations.pop();
            if (operation.type.equals(Operation.ADD)) {
                current.remove(operation.index);
            } else if (operation.type.equals(Operation.REMOVE)) {
                current.add(operation.index, operation.origin);
            } else if (operation.type.equals(Operation.SET)) {
                current.set(operation.index, operation.origin);
            }
        }
        operations.clear();
        for (E e : current) {
            if (e instanceof Data) {
                ((Data) e).rollback();
            }
        }
    }

    /**
     * 检查索引
     *
     * @param index
     * @param add   true：添加操作，false：非添加操作（删除、设置）
     */
    private void checkIndex(int index, boolean add) {
        int indexMax = size();
        if (!add) {
            indexMax = size() - 1;
        }
        if (index < 0 || index > indexMax) {
            throw new IndexOutOfBoundsException("下标越界，index:" + index + ",size" + size());
        }
    }


    private void onAdd(int index) {
        checkIndex(index, true);
        onUpdateData();
        Operation<E> operation = new Operation<>(Operation.ADD, index, null);
        operations.push(operation);

    }

    public void add(int index, E e) {
        onAdd(index);
        current.add(index, e);
        if (e instanceof UpdateCallback) {
            ((UpdateCallback) e).setMappingData(getMappingData());
        }
    }

    private void onSet(int index) {
        checkIndex(index, false);
        onUpdateData();
        E origin = null;
        if (index < current.size()) {
            origin = current.get(index);
        }
        Operation<E> operation = new Operation<>(Operation.SET, index, origin);
        operations.push(operation);

    }

    public E set(int index, E e) {
        onSet(index);
        E old = current.set(index, e);
        if (e instanceof UpdateCallback) {
            ((UpdateCallback) e).setMappingData(getMappingData());
        }
        return old;
    }

    private void onRemove(int index) {
        checkIndex(index, false);
        onUpdateData();
        E origin = null;
        if (index < current.size()) {
            origin = current.get(index);
        }
        Operation<E> operation = new Operation<>(Operation.REMOVE, index, origin);
        operations.push(operation);
    }

    @Override
    public E remove(int index) {
        onRemove(index);
        return current.remove(index);
    }

    public boolean remove(Object o) {
        int index = current.indexOf(o);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public E get(int index) {
        return current.get(index);
    }

    @Override
    public int size() {
        return current.size();
    }

    @Override
    public Iterator iterator() {
        return new InnerIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return new InnerListIterator(index);
    }

    private class InnerIterator implements Iterator<E> {

        int cursor = 0;
        int lastRet = -1;
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public E next() {
            checkForComodification();
            try {
                int i = cursor;
                E next = get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ListWrapper.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class InnerListIterator extends InnerIterator implements ListIterator<E> {
        InnerListIterator(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public E previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                E previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ListWrapper.this.set(lastRet, e);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                ListWrapper.this.add(i, e);
                lastRet = -1;
                cursor = i + 1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static class Operation<E> {
        public static final String ADD = "add";
        public static final String SET = "set";
        public static final String REMOVE = "remove";

        private String type;
        private int index;
        private E origin;

        public Operation(String type, int index, E origin) {
            this.type = type;
            this.index = index;
            this.origin = origin;
        }

        @Override
        public String toString() {
            return "Operation{" +
                    "type='" + type + '\'' +
                    ", index=" + index +
                    ", origin=" + origin +
                    '}';
        }
    }

    @Override
    public String toString() {
        return current.toString();
    }

    @Override
    public String toDebugString() {
        String currentStr = "[";
        for (E e : current) {
            if (e instanceof Data) {
                currentStr += "" + ((Data) e).toDebugString() + ", ";
            } else {
                currentStr += "" + e + ", ";
            }
        }
        if (currentStr.endsWith(", ")) {
            currentStr = currentStr.substring(0, currentStr.length() - 2);
        }
        currentStr += "]";
        return "{" +
                "current=" + currentStr +
                ", operations=" + operations +
                '}';
    }

}
