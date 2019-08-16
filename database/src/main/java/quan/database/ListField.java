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

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Iterator<E> it = getValue().iterator();
            private E current;
            private int index = -1;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                current = it.next();
                if (current != null) {
                    index++;
                }
                return current;
            }

            @Override
            public void remove() {
                if (current == null) {
                    throw new IllegalStateException();
                }
                ListField.this.remove(index);
            }
        };
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

        FieldLog<PVector<E>> log = getLog(true);

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().plus(e);

        if (e instanceof Entity) {
            ((Entity) e).setLogRoot(getRoot());
        }

        if (oldData != newData) {
            log.setValue(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        FieldLog<PVector<E>> log = getLog(true);

        PVector<E> oldData = log.getValue();
        for (E e : oldData) {
            if (e.equals(o) && e instanceof Entity) {
                ((Entity) e).setLogRoot(null);
                break;
            }
        }
        PVector<E> newData = log.getValue().minus(o);

        if (oldData != newData) {
            log.setValue(newData);
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
        Iterator<E> iterator = iterator();
        while (iterator.hasNext())
            if (!c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        return modified;
    }

    @Override
    public E set(int index, E element) {
        Validations.validateCollectionValue(element);

        FieldLog<PVector<E>> log = getLog(true);
        PVector<E> oldData = log.getValue();
        log.setValue(log.getValue().with(index, element));

        if (element instanceof Entity) {
            ((Entity) element).setLogRoot(getRoot());
        }

        E old = oldData.get(index);
        if (old instanceof Entity) {
            ((Entity) old).setLogRoot(null);
        }

        return old;
    }

    @Override
    public void add(int index, E element) {
        Validations.validateCollectionValue(element);
        FieldLog<PVector<E>> log = getLog(true);
        log.setValue(log.getValue().plus(index, element));
    }

    @Override
    public E remove(int index) {
        FieldLog<PVector<E>> log = getLog(true);

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

    @Override
    public ListIterator<E> listIterator() {
        return getValue().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return getValue().listIterator(index);
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
