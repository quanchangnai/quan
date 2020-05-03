package quan.data.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.data.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings("unchecked")
public final class ListField<E> extends Node implements List<E>, Field {

    private PVector<E> list = Empty.vector();

    private Delegate<E> delegate = new Delegate<>(this);

    public ListField(Data<?> root) {
        _setRoot(root);
    }

    public PVector<E> getList() {
        return list;
    }

    public List<E> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object log) {
        this.list = ((Log<E>) log).list;
    }

    @Override
    public void _setChildrenLogRoot(Data<?> root) {
        for (E e : getLogList()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    private Log<E> getLog(boolean write) {
        return getLog(Transaction.check(), write);
    }

    private Log<E> getLog(Transaction transaction, boolean write) {
        if (transaction == null) {
            return null;
        }

        Log<E> log = (Log<E>) _getFieldLog(transaction, this);
        if (write && log == null) {
            log = new Log<>(this.list);
            _setFieldLog(transaction, this, log, _getLogRoot(transaction));
        }
        return log;
    }

    private PVector<E> getLogList() {
        Log<E> log = getLog(false);
        if (log != null) {
            return log.list;
        }
        return list;
    }

    private int getModCount() {
        Log<E> log = getLog(false);
        if (log == null) {
            return 0;
        }
        return log.modCount;
    }

    @Override
    public int size() {
        return getLogList().size();
    }

    @Override
    public boolean isEmpty() {
        return getLogList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getLogList().contains(o);
    }

    private class It implements Iterator<E> {

        int cursor;

        int last = -1;

        int expectedModCount = getModCount();

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public E next() {
            checkConcurrentModification();
            try {
                E next = get(cursor);
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
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }

        }

        void checkConcurrentModification() {
            if (getModCount() != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }

    @Override
    public Object[] toArray() {
        return getLogList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLogList().toArray(a);
    }

    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.check();
        Log<E> log = getLog(transaction, true);

        log.modCount++;
        log.list = log.list.plus(e);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot(transaction));
        }

        return true;
    }

    public boolean plus(E e) {
        Validations.validateCollectionValue(e);

        list = list.plus(e);
        if (e instanceof Entity) {
            _setRoot((Entity) e, _getRoot());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        Log<E> log = getLog(true);
        PVector<E> oldList = log.list;

        log.modCount++;
        log.list = oldList.minus(o);

        if (oldList == log.list) {
            return false;
        }

        if (o instanceof Entity) {
            _setLogRoot((Entity) o, null);
        }

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getLogList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.check();
        Log<E> log = getLog(transaction, true);

        PVector<E> oldList = log.list;
        log.list = oldList.plusAll(index, c);

        if (oldList == log.list) {
            return false;
        }

        Data<?> root = _getLogRoot(transaction);

        for (E e : c) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }

        return true;
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
        Log<E> log = getLog(true);
        if (log.list.isEmpty()) {
            return;
        }

        log.modCount++;
        _setChildrenLogRoot(null);
        log.list = Empty.vector();
    }

    @Override
    public E get(int index) {
        return getLogList().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);

        boolean modified = false;
        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            if (!c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public E set(int index, E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.check();
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
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.check();
        Log<E> log = getLog(transaction, true);

        log.modCount++;
        log.list = log.list.plus(index, e);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot(transaction));
        }
    }

    @Override
    public E remove(int index) {
        Log<E> log = getLog(true);
        E old = log.list.get(index);

        log.modCount++;
        log.list = log.list.minus(index);

        if (old instanceof Entity) {
            _setLogRoot((Entity) old, null);
        }

        return old;
    }

    @Override
    public int indexOf(Object o) {
        return getLogList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getLogList().lastIndexOf(o);
    }

    private class ListIt extends It implements ListIterator<E> {

        public ListIt(int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public E previous() {
            checkConcurrentModification();
            try {
                E previous = get(cursor - 1);
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
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
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
        return getLogList().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogList());
    }

    private static class Log<E> {

        private PVector<E> list;

        private int modCount;

        public Log(PVector<E> list) {
            this.list = list;
        }

    }

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
