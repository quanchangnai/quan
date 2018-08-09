package quan.mongo;

import java.util.*;

/**
 * List包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class ListWrapper<E> extends AbstractList<E> implements Container {

    //当前数据
    private List<E> current = new ArrayList<>();

    //记录所有(add,remove,set)操作
    private Deque<Operation<E>> operations = new ArrayDeque<>();

    /**
     * 当前拥有者
     */
    private MappingData currentOwner;

    /**
     * 原始拥有者
     */
    private MappingData originOwner;

    public ListWrapper(MappingData owner) {
        this.currentOwner = owner;
        this.originOwner = owner;
    }

    /**
     * 不要手动调用
     *
     * @param owner
     */
    public void setOwner(MappingData owner) {
        this.currentOwner = owner;
    }

    public MappingData getOwner() {
        return currentOwner;
    }

    public void commit() {
        originOwner = currentOwner;
        operations.clear();
        for (E e : current) {
            if (e instanceof ReferenceData) {
                ((ReferenceData) e).commit();
            }
        }
    }

    public void rollback() {
        currentOwner = originOwner;
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
            if (e instanceof ReferenceData) {
                ((ReferenceData) e).rollback();
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


    public void add(int index, E e) {
        checkIndex(index, true);
        checkUpdateData(true, e);

        Operation<E> operation = new Operation<>(Operation.ADD, index, null);
        operations.push(operation);

        current.add(index, e);

        if (e instanceof ReferenceData) {
            ((ReferenceData) e).setOwner(getOwner());
        }
    }

    public E set(int index, E e) {
        //校验
        checkIndex(index, false);
        checkUpdateData(true, e);

        //记录操作
        E origin = null;
        if (index < current.size()) {
            origin = current.get(index);
        }
        Operation<E> operation = new Operation<>(Operation.SET, index, origin);
        operations.push(operation);

        //替换数据
        E old = current.set(index, e);

        if (e instanceof Container) {
            ((Container) e).setOwner(getOwner());
        }
        if (old instanceof Container) {
            ((Container) e).setOwner(null);
        }
        return old;
    }

    @Override
    public E remove(int index) {
        //校验
        checkIndex(index, false);
        checkUpdateData(false, null);
        E origin = null;
        if (index < current.size()) {
            origin = current.get(index);
        }
        Operation<E> operation = new Operation<>(Operation.REMOVE, index, origin);
        operations.push(operation);

        //删除数据
        E value = current.remove(index);
        if (value instanceof Container) {
            ((Container) value).setOwner(null);
        }
        return value;
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

    public String toDebugString() {
        String currentStr = "[";
        for (E e : current) {
            if (e instanceof ReferenceData) {
                currentStr += "" + ((ReferenceData) e).toDebugString() + ", ";
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
                ", owner=" + getOwner() +
                '}';
    }

}
