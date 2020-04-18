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
@SuppressWarnings("unchecked")
public final class SetField<E> extends Node implements Set<E>, Field {

    private PSet<E> set = Empty.set();

    private Delegate<E> delegate = new Delegate<>(this);


    public SetField(Data<?> root) {
        _setRoot(root);
    }

    public PSet<E> getSet() {
        return set;
    }

    public Delegate<E> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object logSet) {
        this.set = (PSet<E>) logSet;
    }

    @Override
    public void _setChildrenLogRoot(Data<?> root) {
        for (E e : getLogSet()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    private PSet<E> getLogSet(Transaction transaction) {
        PSet<E> log = (PSet<E>) _getFieldLog(transaction, this);
        if (log != null) {
            return log;
        }
        return set;
    }

    private PSet<E> getLogSet() {
        return getLogSet(Transaction.get(false));
    }

    @Override
    public int size() {
        return getLogSet().size();
    }

    @Override
    public boolean isEmpty() {
        return getLogSet().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getLogSet().contains(o);
    }


    private class It implements Iterator<E> {

        private Iterator<E> it = getLogSet().iterator();

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
        return getLogSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLogSet().toArray(a);
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.get(true);
        PSet<E> oldSet = getLogSet(transaction);
        PSet<E> newSet = oldSet.plus(e);

        if (oldSet == newSet) {
            return false;
        }

        Data<?> root = _getLogRoot(transaction);

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
        PSet<E> oldSet = getLogSet(transaction);
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
        return getLogSet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.get(true);
        PSet<E> oldSet = getLogSet(transaction);
        PSet<E> newSet = oldSet.plusAll(c);

        if (oldSet == newSet) {
            return false;
        }

        Data<?> root = _getLogRoot(transaction);

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
        if (getLogSet(transaction).isEmpty()) {
            return;
        }

        _setChildrenLogRoot(null);
        _setFieldLog(transaction, this, Empty.set(), _getLogRoot(transaction));
    }

    @Override
    public String toString() {
        return String.valueOf(getLogSet());
    }


    private static class Delegate<E> implements Set<E> {

        private SetField<E> field;

        public Delegate(SetField<E> field) {
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
        public boolean removeAll(Collection<?> c) {
            return field.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return field.retainAll(c);
        }

        @Override
        public void clear() {
            field.clear();
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }
}
