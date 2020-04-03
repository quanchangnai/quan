package quan.database;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked"})
public final class ListField<E> extends Node implements List<E>, Field<ArrayList<E>> {

    private ArrayList<E> _list = new ArrayList<>();

    public ListField(Data root) {
        _setRoot(root);
    }

    @Override
    public void _setChildrenLogRoot(Data root) {
        for (E e : getValue()) {
            if (e instanceof Entity) {
                ((Entity) e)._setLogRoot(root);
            }
        }
    }

    @Override
    public void setValue(ArrayList<E> value) {
        this._list = value;
    }

    @Override
    public ArrayList<E> getValue() {
        return _list;
    }

    private int getModCount() {
        return 0;
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
        return getValue().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getValue().toArray(a);
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);
        return getValue().add(e);
    }

    @Override
    public boolean remove(Object o) {
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

        return getValue().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        for (E e : c) {
            Validations.validateCollectionValue(e);
        }

        return getValue().addAll(index, c);
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
        getValue().clear();
    }

    @Override
    public E get(int index) {
        return getValue().get(index);
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
        return getValue().set(index, e);
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);
        getValue().add(index, e);
    }

    @Override
    public E remove(int index) {
        return getValue().remove(index);
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
        return getValue().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
