package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PSet;
import quan.database.*;
import quan.database.log.FieldLog;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked"})
public final class SetField<E> extends Node implements Set<E>, Field<PSet<E>> {

    private PSet<E> set = Empty.set();

    public SetField(Data root) {
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

    @Override
    public void setValue(PSet<E> set) {
        this.set = set;
    }

    @Override
    public PSet<E> getValue() {
        return set;
    }

    @Override
    public PSet<E> getLogValue() {
        FieldLog<PSet<E>> log = getLog(false);
        if (log != null) {
            return log.getValue();
        }
        return set;
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

        private Iterator<E> it = getLogValue().iterator();

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
        return getLogValue().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLogValue().toArray(a);
    }

    private FieldLog<PSet<E>> getLog(boolean add) {
        Transaction transaction = Transaction.get(add);
        if (transaction == null) {
            return null;
        }

        FieldLog<PSet<E>> log = (FieldLog<PSet<E>>) transaction.getFieldLog(this);
        if (add && log == null) {
            log = new FieldLog<>(this, set);
            transaction.addFieldLog(log);
            transaction.addDataLog(_getLogRoot());
        }

        return log;
    }


    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);

        FieldLog<PSet<E>> log = getLog(true);

        PSet<E> oldSet = log.getValue();
        PSet<E> newSet = oldSet.plus(e);

        if (oldSet == newSet) {
            return false;
        }

        log.setValue(newSet);
        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }

        return true;
    }

    public boolean _add(E e) {
        Validations.validateCollectionValue(e);
        PSet<E> oldSet = set;
        set = set.plus(e);

        if (oldSet == set) {
            return false;
        }

        if (e instanceof Entity) {
            _setLogRoot((Entity) e, _getLogRoot());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        FieldLog<PSet<E>> log = getLog(true);

        PSet<E> oldSet = log.getValue();
        PSet<E> newSet = oldSet.minus(o);

        if (oldSet == newSet) {
            return false;
        }

        log.setValue(newSet);
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
        Objects.requireNonNull(c);
        for (E e : c) {
            Validations.validateCollectionValue(e);
        }

        FieldLog<PSet<E>> log = getLog(true);
        PSet<E> oldSet = log.getValue();
        log.setValue(oldSet.plusAll(c));

        for (E e : c) {
            if (e instanceof Entity) {
                _setLogRoot((Entity) e, _getLogRoot());
            }
        }

        return oldSet != log.getValue();
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
        return String.valueOf(getLogValue());
    }

}
