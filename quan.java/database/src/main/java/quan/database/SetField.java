package quan.database;

import org.pcollections.Empty;
import org.pcollections.PSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked"})
public final class SetField<E> extends Node implements Set<E>, Field<PSet<E>> {

    private PSet<E> data = Empty.set();

    public SetField(Data root) {
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
    public void setValue(PSet<E> data) {
        this.data = data;
    }

    @Override
    public PSet<E> getValue() {
        FieldLog<PSet<E>> log = getLog(false);
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

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                return current = it.next();
            }

            @Override
            public void remove() {
                if (current == null) {
                    throw new IllegalStateException();
                }
                SetField.this.remove(current);
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

    private FieldLog<PSet<E>> getLog(boolean add) {
        Transaction transaction = Transaction.get(add);
        if (transaction == null) {
            return null;
        }

        FieldLog<PSet<E>> log = (FieldLog<PSet<E>>) transaction.getFieldLog(this);
        if (add && log == null) {
            log = new FieldLog<>(this, data);
            transaction.addFieldLog(log);
            transaction.addVersionLog(_getRoot());
        }

        return log;
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        FieldLog<PSet<E>> log = getLog(true);

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.plus(e);

        if (e instanceof Entity) {
            ((Entity) e)._setLogRoot(_getRoot());
        }

        if (oldData != newData) {
            log.setValue(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        FieldLog<PSet<E>> log = getLog(true);

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.minus(o);

        for (E e : oldData) {
            if (e.equals(o) && e instanceof Entity) {
                ((Entity) e)._setLogRoot(null);
            }
        }

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
        Objects.requireNonNull(c);
        for (E e : c) {
            Validations.validateCollectionValue(e);
        }

        FieldLog<PSet<E>> log = getLog(true);
        PSet<E> oldData = log.getValue();
        log.setValue(oldData.plusAll(c));

        for (E e : c) {
            if (e instanceof Entity) {
                ((Entity) e)._setLogRoot(_getRoot());
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
    public void clear() {
        FieldLog<PSet<E>> log = getLog(true);
        if (log.getValue().isEmpty()) {
            return;
        }
        _setChildrenLogRoot(null);
        log.setValue(Empty.set());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
