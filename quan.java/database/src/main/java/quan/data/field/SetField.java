package quan.data.field;

import org.pcollections.Empty;
import org.pcollections.PSet;
import quan.data.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public final class SetField<E> extends Node implements Set<E>, Field {

    private PSet<E> set = Empty.set();

    private Delegate<E> delegate = new Delegate<>(this);


    public SetField(Data<?> root) {
        _setRoot(root);
    }

    public Set<E> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object log) {
        this.set = (PSet<E>) log;
    }

    @Override
    public void _setChildrenLogRoot(Data<?> root) {
        for (E e : getSet()) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, root);
            }
        }
    }

    private PSet<E> getSet(Transaction transaction) {
        if (transaction != null) {
            PSet<E> log = (PSet<E>) _getFieldLog(transaction, this);
            if (log != null) {
                return log;
            }
        }
        return set;
    }

    private PSet<E> getSet() {
        return getSet(Transaction.get());
    }

    @Override
    public int size() {
        return getSet().size();
    }

    @Override
    public boolean isEmpty() {
        return getSet().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getSet().contains(o);
    }


    private class It implements Iterator<E> {

        private Iterator<E> it = getSet().iterator();

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
        return getSet().toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a) {
        return getSet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            PSet<E> oldSet = getSet(transaction);
            PSet<E> newSet = oldSet.plus(e);

            if (oldSet != newSet) {
                Data<?> root = _getLogRoot(transaction);
                _setFieldLog(transaction, this, newSet, root);
                if (e instanceof Entity) {
                    _setLogRoot((Entity) e, root);
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            return plus(e);
        } else {
            Transaction.error();
        }

        return false;
    }

    public boolean plus(E e) {
        Validations.validateCollectionValue(e);

        PSet<E> oldSet = set;
        set = oldSet.plus(e);

        if (oldSet != set) {
            if (e instanceof Entity) {
                _setRoot((Entity) e, _getRoot());
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            PSet<E> oldSet = getSet(transaction);
            PSet<E> newSet = oldSet.minus(o);
            if (oldSet != newSet) {
                _setFieldLog(transaction, this, newSet, _getLogRoot(transaction));
                if (o instanceof Entity) {
                    _setLogRoot((Entity) o, null);
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PSet<E> oldSet = set;
            set = set.minus(o);
            if (oldSet != set) {
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
        return getSet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            PSet<E> oldSet = getSet(transaction);
            PSet<E> newSet = oldSet.plusAll(c);
            if (oldSet != newSet) {
                Data<?> root = _getLogRoot(transaction);
                _setFieldLog(transaction, this, newSet, root);
                for (E e : c) {
                    if (e instanceof Entity) {
                        _setLogRoot((Entity) e, root);
                    }
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PSet<E> oldSet = set;
            set = oldSet.plusAll(c);
            if (oldSet != set) {
                Data<?> root = _getRoot();
                for (E e : c) {
                    if (e instanceof Entity) {
                        _setLogRoot((Entity) e, root);
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
        Transaction transaction = Transaction.get();
        PSet<E> oldSet = getSet(transaction);
        if (oldSet.isEmpty()) {
            return;
        }

        if (transaction != null) {
            _setChildrenLogRoot(null);
            _setFieldLog(transaction, this, Empty.set(), _getLogRoot(transaction));
        } else if (Transaction.isOptional()) {
            for (E e : oldSet) {
                if (e instanceof Entity) {
                    _setRoot((Entity) e, null);
                }
            }
            this.set = Empty.set();
        } else {
            Transaction.error();
        }

    }

    @Override
    public String toString() {
        return String.valueOf(getSet());
    }

    @SuppressWarnings("NullableProblems")
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
