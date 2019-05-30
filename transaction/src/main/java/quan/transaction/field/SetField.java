package quan.transaction.field;

import org.pcollections.Empty;
import org.pcollections.PSet;
import quan.transaction.BeanData;
import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.log.SetLog;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
public class SetField<E> extends BeanData implements Set<E>, Field {

    private PSet<E> data = Empty.set();

    public SetField(MappingData root) {
        setRoot(root);
    }

    @Override
    protected void setChildrenLogRoot(MappingData root) {
        for (E e : getData()) {
            if (e instanceof BeanData) {
                ((BeanData) e).setLogRoot(root);
            }
        }
    }

    public SetField<E> setData(PSet<E> data) {
        this.data = data;
        return this;
    }

    public PSet<E> getData() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            SetLog<E> log = (SetLog<E>) transaction.getFieldLog(this);
            if (log != null) {
                return log.getData();
            }
        }
        return data;
    }

    @Override
    public int size() {
        return getData().size();
    }

    @Override
    public boolean isEmpty() {
        return getData().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getData().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getData().iterator();
    }

    @Override
    public Object[] toArray() {
        return getData().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getData().toArray(a);
    }

    private SetLog<E> getOrAddLog() {
        checkTransaction();
        Transaction transaction = Transaction.current();
        SetLog<E> log = (SetLog<E>) transaction.getFieldLog(this);
        if (log == null) {
            log = new SetLog<>(this);
            transaction.addFieldLog(log);
        }
        return log;
    }



    @Override
    public boolean add(E e) {
        validValue(e);

        SetLog<E> log = getOrAddLog();

        PSet<E> oldData = log.getData();
        PSet<E> newData = oldData.plus(e);

        if (e instanceof BeanData) {
            ((BeanData) e).setLogRoot(getRoot());
        }

        if (oldData != newData) {
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        SetLog<E> log = getOrAddLog();

        PSet<E> oldData = log.getData();
        PSet<E> newData = oldData.minus(o);

        for (E e : oldData) {
            if (e.equals(o) && e instanceof BeanData) {
                ((BeanData) e).setLogRoot(null);
            }
        }

        if (oldData != newData) {
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getData().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            validValue(e);
        }

        SetLog<E> log = getOrAddLog();

        PSet<E> oldData = log.getData();
        PSet<E> newData = oldData.plusAll(c);

        for (E e : c) {
            if (e instanceof BeanData) {
                ((BeanData) e).setLogRoot(getRoot());
            }
        }

        if (oldData != newData) {
            log.setData(newData);
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
        SetLog<E> log = getOrAddLog();

        PSet<E> oldData = log.getData();
        PSet<E> newData = oldData.minusAll(c);

        if (oldData != newData) {
            for (E e : oldData) {
                if (!newData.contains(e) && e instanceof BeanData) {
                    ((BeanData) e).setLogRoot(null);
                }
            }
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        SetLog<E> log = getOrAddLog();
        if (log.getData().isEmpty()) {
            return;
        }
        setChildrenLogRoot(null);
        log.setData(Empty.set());
    }

    @Override
    public String toString() {
        return getData().toString();
    }
}
