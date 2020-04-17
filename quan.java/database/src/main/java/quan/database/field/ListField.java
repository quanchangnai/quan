package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.database.*;
import quan.database.log.ListLog;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked"})
public final class ListField<E> extends Node implements List<E>, Field {

    private PVector<E> list = Empty.vector();

    public ListField(Data root) {
        _setRoot(root);
    }

    @Override
    public void _setChildrenLogRoot(Data root) {
        for (E e : getLogValue()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    public PVector<E> getValue() {
        return list;
    }

    public void setValue(PVector<E> list) {
        this.list = list;
    }

    @Override
    public void setValue(Object list) {
        this.list = (PVector<E>) list;
    }

    public PVector<E> getLogValue() {
        PVector<E> log = (PVector<E>) _getFieldLog(Transaction.get(true), this);
        if (log != null) {
            return log;
        }
        return list;
    }

    private int getModCount() {
        return 0;
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
    public boolean contains(Object o) {
        return getLogValue().contains(o);
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
        return getLogValue().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLogValue().toArray(a);
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        PVector<E> log = getLogValue();
//        log.incModCount();

        _addFieldLog(Transaction.get(true), this, log.plus(e), null);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }

        return true;
    }

    public boolean _add(E e) {
        Validations.validateCollectionValue(e);

        list = list.plus(e);
        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.minus(o);

        if (oldList == newList) {
            return false;
        }

        _addFieldLog(Transaction.get(true), this, newList, null);

        if (o instanceof Entity) {
            _setLogRoot((Entity) o, null);
        }

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getLogValue().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(Validations::validateCollectionValue);

        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.plusAll(c);

        if (oldList == newList) {
            return false;
        }

        for (E e : c) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot());
            }
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.plusAll(index, c);

        if (oldList == newList) {
            return false;
        }

        for (E e : c) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot());
            }
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
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
        if (getLogValue().isEmpty()) {
            return;
        }

        _setChildrenLogRoot(null);
        _addFieldLog(Transaction.get(true), this, Empty.vector(), null);
    }

    @Override
    public E get(int index) {
        return getLogValue().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> iterator = iterator();
        while (iterator.hasNext())
            if (!c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        return modified;
    }

    @Override
    public E set(int index, E e) {
        Validations.validateCollectionValue(e);

        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.with(index, e);
        _addFieldLog(Transaction.get(true), this, newList, null);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }

        E old = oldList.get(index);
        if (old instanceof Entity) {
            _setLogRoot((Entity) e, null);
        }

        return old;
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);

        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.plus(index, e);
        _addFieldLog(Transaction.get(true), this, newList, null);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }
    }

    @Override
    public E remove(int index) {
        PVector<E> oldList = getLogValue();
        PVector<E> newList = oldList.minus(index);
        _addFieldLog(Transaction.get(true), this, newList, null);

        E old = oldList.get(index);
        if (old instanceof Entity) {
            _setLogRoot((Entity) old, null);
        }

        return old;
    }

    @Override
    public int indexOf(Object o) {
        return getLogValue().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getLogValue().lastIndexOf(o);
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
        return getLogValue().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getLogValue());
    }
}
