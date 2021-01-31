package quan.data.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.data.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public final class ListField<E> extends Node implements List<E>, Field {

    private PVector<E> list = Empty.vector();

    private Delegate<E> delegate = new Delegate<>(this);

    private int modCount;

    public ListField(Data<?> root) {
        _setRoot(root);
    }

    public List<E> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object log) {
        Log<E> log1 = (Log<E>) log;
        list = log1.list;
        modCount = log1.modCount;
    }

    @Override
    public void _setChildrenLogRoot(Data<?> root) {
        for (E e : getList()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    private Log<E> getLog(boolean write) {
        return getLog(Transaction.get(), write);
    }

    private Log<E> getLog(Transaction transaction, boolean write) {
        if (transaction == null) {
            return null;
        }

        Log<E> log = (Log<E>) _getFieldLog(transaction, this);
        if (write && log == null) {
            log = new Log<>(list, modCount);
            _setFieldLog(transaction, this, log, _getLogRoot(transaction));
        }

        return log;
    }

    private PVector<E> getList() {
        Log<E> log = getLog(false);
        if (log != null) {
            return log.list;
        }
        return list;
    }

    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getList().contains(o);
    }

    private class It implements Iterator<E> {

        //提前取出，避免遍历过程中频繁调用
        Transaction transaction = Transaction.get();

        int cursor;

        int last = -1;

        int expectedModCount = getModCount();

        Log<E> log;

        PVector<E> list = log != null ? log.list : ListField.this.list;

        //如果迭代过程中没有修改，用这个比较快
        Iterator<E> iterator = list.iterator();

        @Override
        public boolean hasNext() {
            return cursor != this.list.size();
        }

        @Override
        public E next() {
            checkConcurrentModification();
            try {
                E next;
                if (iterator != null) {
                    next = iterator.next();
                } else {
                    next = this.list.get(cursor);
                }
                last = cursor;
                cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkConcurrentModification();
                throw new NoSuchElementException();
            }

        }

        @Override
        public void remove() {
            if (last < 0) {
                throw new IllegalStateException();
            }
            checkConcurrentModification();
            try {
                ListField.this.remove(last);
                if (last < cursor) {
                    cursor--;
                }
                last--;
                expectedModCount = getModCount();
                this.list = log.list;
                clearIterator();
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }

        }

        int getModCount() {
            if (transaction != null && log == null) {
                log = getLog(transaction, false);
            }
            if (log != null) {
                return log.modCount;
            }
            return ListField.this.modCount;
        }

        void checkConcurrentModification() {
            if (getModCount() != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        void clearIterator() {
            iterator = null;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }

    @Override
    public Object[] toArray() {
        return getList().toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a) {
        return getList().toArray(a);
    }

    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            log.modCount++;
            log.list = log.list.plus(e);
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot(transaction));
            }
        } else if (Transaction.isOptional()) {
            return plus(e);
        } else {
            Transaction.error();
        }

        return true;
    }

    public boolean plus(E e) {
        Validations.validateCollectionValue(e);

        modCount++;
        list = list.plus(e);
        if (e instanceof Entity) {
            _setRoot((Entity) e, _getRoot());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (Transaction.isInside()) {
            Log<E> log = getLog(true);
            PVector<E> oldList = log.list;
            log.modCount++;
            log.list = oldList.minus(o);

            if (oldList != log.list) {
                if (o instanceof Entity) {
                    _setLogRoot((Entity) o, null);
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = list;
            modCount++;
            list = oldList.minus(o);

            if (oldList != list) {
                if (o instanceof Entity) {
                    _setRoot((Entity) o, null);
                }
                return true;
            }
        } else {
            Transaction.error();
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            PVector<E> oldList = log.list;
            log.list = oldList.plusAll(index, c);
            log.modCount++;

            if (oldList != log.list) {
                Data<?> root = _getLogRoot(transaction);
                for (E e : c) {
                    if (e instanceof Entity) {
                        _setLogRoot((Entity) e, root);
                    }
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = list;
            modCount++;
            list = oldList.plusAll(index, c);

            if (oldList != list) {
                Data<?> root = _getRoot();
                for (E e : c) {
                    if (e instanceof Entity) {
                        _setRoot((Entity) e, root);
                    }
                }
                return true;
            }
        } else {
            Transaction.error();
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            if (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            if (!log.list.isEmpty()) {
                log.modCount++;
                _setChildrenLogRoot(null);
                log.list = Empty.vector();
            }
        } else if (!Transaction.isOptional()) {
            Transaction.error();
        } else if (!list.isEmpty()) {
            modCount++;
            for (E e : list) {
                if (e instanceof Entity) {
                    _setRoot((Entity) e, null);
                }
            }
            list = Empty.vector();
        }
    }

    @Override
    public E get(int index) {
        return getList().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);

        boolean modified = false;
        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            if (c.contains(iterator.next())) {
                continue;
            }
            iterator.remove();
            modified = true;
        }

        return modified;
    }

    @Override
    public E set(int index, E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            PVector<E> oldList = log.list;
            log.modCount++;
            log.list = oldList.with(index, e);

            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot(transaction));
            }
            E old = oldList.get(index);
            if (old instanceof Entity) {
                _setLogRoot((Entity) old, null);
            }
            return old;
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = list;
            modCount++;
            list = oldList.with(index, e);

            if (e instanceof Entity) {
                _setRoot((Entity) e, _getRoot());
            }
            E old = oldList.get(index);
            if (old instanceof Entity) {
                _setRoot((Entity) old, null);
            }
            return old;
        } else {
            Transaction.error();
            return null;
        }
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            log.modCount++;
            log.list = log.list.plus(index, e);
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot(transaction));
            }
        } else if (Transaction.isOptional()) {
            modCount++;
            list = list.plus(index, e);
            if (e instanceof Entity) {
                _setRoot((Entity) e, _getRoot());
            }
        } else {
            Transaction.error();
        }
    }

    @Override
    public E remove(int index) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            E old = log.list.get(index);
            log.modCount++;
            log.list = log.list.minus(index);

            if (old instanceof Entity) {
                _setLogRoot((Entity) old, null);
            }

            return old;
        } else if (Transaction.isOptional()) {
            E old = list.get(index);
            modCount++;
            list = list.minus(index);

            if (old instanceof Entity) {
                _setRoot((Entity) old, null);
            }

            return old;
        } else {
            Transaction.error();
            return null;
        }
    }

    @Override
    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    private class ListIt extends It implements ListIterator<E> {

        private ListIterator<E> listIterator;

        public ListIt(int index) {
            cursor = index;
            iterator = list.listIterator(index);
            listIterator = (ListIterator<E>) iterator;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public E previous() {
            checkConcurrentModification();
            try {
                E previous;
                if (listIterator != null) {
                    previous = listIterator.previous();
                } else {
                    previous = this.list.get(cursor - 1);
                }
                last = --cursor;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkConcurrentModification();
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(E e) {
            if (last < 0) {
                throw new IllegalStateException();
            }
            checkConcurrentModification();

            try {
                ListField.this.set(last, e);
                expectedModCount = getModCount();
                clearIterator();
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            checkConcurrentModification();

            try {
                ListField.this.add(cursor, e);
                last = -1;
                cursor++;
                expectedModCount = getModCount();
                clearIterator();
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        void clearIterator() {
            super.clearIterator();
            listIterator = null;
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIt(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size());
        }
        return new ListIt(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getList());
    }

    private static class Log<E> {

        private PVector<E> list;

        private int modCount;

        public Log(PVector<E> list, int modCount) {
            this.list = list;
            this.modCount = modCount;
        }

    }

    @SuppressWarnings("NullableProblems")
    private static class Delegate<E> implements List<E> {

        private ListField<E> field;

        public Delegate(ListField<E> field) {
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
        public boolean contains(Object o) {
            return field.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return field.iterator();
        }

        @Override
        public Object[] toArray() {
            return field.toArray();
        }

        @SuppressWarnings("SuspiciousToArrayCall")
        @Override
        public <T> T[] toArray(T[] a) {
            return field.toArray(a);
        }

        @Override
        public boolean add(E e) {
            return field.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return field.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return field.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return field.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return field.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return field.removeAll(c);
        }

        @Override
        public void clear() {
            field.clear();
        }

        @Override
        public E get(int index) {
            return field.get(index);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return field.retainAll(c);
        }

        @Override
        public E set(int index, E e) {
            return field.set(index, e);
        }

        @Override
        public void add(int index, E e) {
            field.add(index, e);
        }

        @Override
        public E remove(int index) {
            return field.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return field.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return field.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            return field.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return field.listIterator(index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return field.subList(fromIndex, toIndex);
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }

}
