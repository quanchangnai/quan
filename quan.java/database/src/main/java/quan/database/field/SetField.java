package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PSet;
import quan.database.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class SetField<E> extends Node implements Set<E>, Field {

    private PSet<E> set = Empty.set();

    public SetField(Data root) {
        _setRoot(root);
    }

    public PSet<E> getValue() {
        return set;
    }

    @Override
    public void commit(Object log) {
        this.set = (PSet<E>) log;
    }

    @Override
    public void _setChildrenLogRoot(Data root) {
        for (E e : getLog()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    private PSet<E> getLog(Transaction transaction) {
        PSet<E> log = (PSet<E>) _getFieldLog(transaction, this);
        if (log != null) {
            return log;
        }
        return set;
    }

    private PSet<E> getLog() {
        return getLog(Transaction.get(false));
    }

    @Override
    public int size() {
        return getLog().size();
    }

    @Override
    public boolean isEmpty() {
        return getLog().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getLog().contains(o);
    }


    private class It implements Iterator<E> {

        private Iterator<E> it = getLog().iterator();

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
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }

    @Override
    public Object[] toArray() {
        return getLog().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLog().toArray(a);
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.get(true);
        PSet<E> oldSet = getLog(transaction);
        PSet<E> newSet = oldSet.plus(e);

        if (oldSet == newSet) {
            return false;
        }

        Data root = _getLogRoot(transaction);

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, root);
        }

        _setFieldLog(transaction, this, newSet, root);

        return true;
    }

    public boolean _add(E e) {
        Validations.validateCollectionValue(e);

        PSet<E> oldSet = set;
        set = oldSet.plus(e);

        if (oldSet == set) {
            return false;
        }

        if (e instanceof Entity) {
            _setRoot((Entity) e, _getRoot());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        Transaction transaction = Transaction.get(true);
        PSet<E> oldSet = getLog(transaction);
        PSet<E> newSet = oldSet.minus(o);

        if (oldSet == newSet) {
            return false;
        }

        _setFieldLog(transaction, this, newSet, _getLogRoot(transaction));

        if (o instanceof Entity) {
            _setLogRoot((Entity) o, null);
        }


        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getLog().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.get(true);
        PSet<E> oldSet = getLog(transaction);
        PSet<E> newSet = oldSet.plusAll(c);

        if (oldSet == newSet) {
            return false;
        }

        Data root = _getLogRoot(transaction);

        _setFieldLog(transaction, this, newSet, root);

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
    public void clear() {
        Transaction transaction = Transaction.get(true);
        if (getLog(transaction).isEmpty()) {
            return;
        }

        _setChildrenLogRoot(null);
        _setFieldLog(transaction, this, Empty.set(), _getLogRoot(transaction));
    }

    @Override
    public String toString() {
        return String.valueOf(getLog());
    }

}
