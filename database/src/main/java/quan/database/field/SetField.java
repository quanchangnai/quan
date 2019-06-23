package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PSet;
import quan.database.Bean;
import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.FieldLog;
import quan.database.util.Validations;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/21.
 */
public class SetField<E> extends Bean implements Set<E>, Field<PSet<E>> {

    private PSet<E> data = Empty.set();

    public SetField(Data root) {
        setRoot(root);
    }

    @Override
    public void setChildrenLogRoot(Data root) {
        for (E e : getValue()) {
            if (e instanceof Bean) {
                ((Bean) e).setLogRoot(root);
            }
        }
    }

    @Override
    public void setValue(PSet<E> data) {
        this.data = data;
    }

    @Override
    public PSet<E> getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            FieldLog<PSet<E>> log = (FieldLog<PSet<E>>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getValue();
            }
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
        return getValue().iterator();
    }

    @Override
    public Object[] toArray() {
        return getValue().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getValue().toArray(a);
    }

    private FieldLog<PSet<E>> getOrAddLog() {
        Transaction transaction = Validations.validTransaction();
        FieldLog<PSet<E>> log = (FieldLog<PSet<E>>) transaction.getFieldLog(this);
        if (log == null) {
            log = new FieldLog<>(this,data);
            transaction.addFieldLog(log);
        }
        return log;
    }


    @Override
    public boolean add(E e) {
        Validations.validCollectionValue(e);

        FieldLog<PSet<E>> log = getOrAddLog();

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.plus(e);

        if (e instanceof Bean) {
            ((Bean) e).setLogRoot(getRoot());
        }

        if (oldData != newData) {
            log.setValue(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        FieldLog<PSet<E>> log = getOrAddLog();

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.minus(o);

        for (E e : oldData) {
            if (e.equals(o) && e instanceof Bean) {
                ((Bean) e).setLogRoot(null);
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
        for (E e : c) {
            Validations.validCollectionValue(e);
        }

        FieldLog<PSet<E>> log = getOrAddLog();

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.plusAll(c);

        for (E e : c) {
            if (e instanceof Bean) {
                ((Bean) e).setLogRoot(getRoot());
            }
        }

        if (oldData != newData) {
            log.setValue(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        FieldLog<PSet<E>> log = getOrAddLog();

        PSet<E> oldData = log.getValue();
        PSet<E> newData = oldData.minusAll(c);

        if (oldData != newData) {
            for (E e : oldData) {
                if (!newData.contains(e) && e instanceof Bean) {
                    ((Bean) e).setLogRoot(null);
                }
            }
            log.setValue(newData);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        FieldLog<PSet<E>> log = getOrAddLog();
        if (log.getValue().isEmpty()) {
            return;
        }
        setChildrenLogRoot(null);
        log.setValue(Empty.set());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
