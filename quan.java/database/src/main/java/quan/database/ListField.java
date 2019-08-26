package quan.database;

import org.pcollections.Empty;
import org.pcollections.PVector;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked"})
public final class ListField<E> extends Node implements List<E>, Field<PVector<E>> {

    private PVector<E> data = Empty.vector();

    private int modCount;

    public ListField(Data root) {
        setRoot(root);
    }

    @Override
    public void setChildrenLogRoot(Data root) {
        for (E e : getValue()) {
            if (e instanceof Entity) {
                ((Entity) e).setLogRoot(root);
            }
        }
    }

    @Override
    public void setValue(PVector<E> data) {
        modCount++;
        this.data = data;
    }

    @Override
    public PVector<E> getValue() {
        FieldLog<PVector<E>> log = getLog(false);
        if (log != null) {
            return log.getValue();
        }
        return data;
    }

    @Override
    public int size() {
        return getValue().size();
    }

    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getValue().contains(o);
    }


    private class It implements Iterator<E> {

        int cursor;

        int last = -1;

        int expectedModCount = modCount;

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
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }

        }

        void checkConcurrentModification() {
            if (modCount != expectedModCount) {
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
        return getValue().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getValue().toArray(a);
    }

    private FieldLog<PVector<E>> getLog(boolean add) {
        Transaction transaction = Transaction.get(add);
        if (transaction == null) {
            return null;
        }

        FieldLog<PVector<E>> log = (FieldLog<PVector<E>>) transaction.getFieldLog(this);
        if (add && log == null) {
            log = new FieldLog<>(this, data);
            transaction.addFieldLog(log);
            transaction.addVersionLog(getRoot());
        }

        return log;
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        modCount++;
        FieldLog<PVector<E>> log = getLog(true);
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().plus(e));

        if (e instanceof Entity) {
            ((Entity) e).setLogRoot(getRoot());
        }

        return oldData != log.getValue();
    }

    @Override
    public boolean remove(Object o) {
        FieldLog<PVector<E>> log = getLog(true);

        modCount++;
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().minus(o));

        if (oldData != log.getValue()) {
            for (E e : oldData) {
                if (e.equals(o) && e instanceof Entity) {
                    ((Entity) e).setLogRoot(null);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getValue().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            Validations.validateCollectionValue(e);
        }

        modCount++;
        FieldLog<PVector<E>> log = getLog(true);
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().plusAll(c));

        for (E e : c) {
            if (e instanceof Entity) {
                ((Entity) e).setLogRoot(getRoot());
            }
        }

        return oldData != log.getValue();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        for (E e : c) {
            Validations.validateCollectionValue(e);
        }

        modCount++;
        FieldLog<PVector<E>> log = getLog(true);
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().plusAll(index, c));

        for (E e : c) {
            if (e instanceof Entity) {
                ((Entity) e).setLogRoot(getRoot());
            }
        }

        return oldData != log.getValue();
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
        FieldLog<PVector<E>> log = getLog(true);
        if (log.getValue().isEmpty()) {
            return;
        }
        modCount++;
        setChildrenLogRoot(null);
        log.setValue(Empty.vector());
    }

    @Override
    public E get(int index) {
        return getValue().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        modCount++;
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

        modCount++;
        FieldLog<PVector<E>> log = getLog(true);
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().with(index, e));

        if (e instanceof Entity) {
            ((Entity) e).setLogRoot(getRoot());
        }

        E old = oldData.get(index);
        if (old instanceof Entity) {
            ((Entity) old).setLogRoot(null);
        }

        return old;
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);

        modCount++;
        FieldLog<PVector<E>> log = getLog(true);
        log.setValue(log.getValue().plus(index, e));

        if (e instanceof Entity) {
            ((Entity) e).setLogRoot(getRoot());
        }
    }

    @Override
    public E remove(int index) {
        FieldLog<PVector<E>> log = getLog(true);

        modCount++;
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().minus(index));

        E old = oldData.get(index);
        if (old instanceof Entity) {
            ((Entity) old).setLogRoot(null);
        }

        return old;
    }

    @Override
    public int indexOf(Object o) {
        return getValue().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getValue().lastIndexOf(o);
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
                expectedModCount = modCount;
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
                expectedModCount = modCount;
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
        return getValue().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
