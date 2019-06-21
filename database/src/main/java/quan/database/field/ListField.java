package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.database.Bean;
import quan.database.Data;
import quan.database.Transaction;
import quan.database.log.ListLog;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by quanchangnai on 2019/5/21.
 */
public class ListField<E> extends Bean implements List<E>, Field {

    private PVector<E> data = Empty.vector();

    public ListField(Data root) {
        setRoot(root);
    }

    @Override
    protected void setChildrenLogRoot(Data root) {
        for (E e : getData()) {
            if (e instanceof Bean) {
                ((Bean) e).setLogRoot(root);
            }
        }
    }

    public ListField<E> setData(PVector<E> data) {
        this.data = data;
        return this;
    }

    public PVector<E> getData() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            ListLog<E> log = (ListLog<E>) transaction.getFieldLog(this);
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

    private ListLog<E> getOrAddLog() {
        Transaction transaction = checkTransaction();
        if (getRoot() != null) {
            transaction.addVersionLog(getRoot());
        }
        ListLog<E> log = (ListLog<E>) transaction.getFieldLog(this);
        if (log == null) {
            log = new ListLog<>(this);
            transaction.addFieldLog(log);
        }
        return log;
    }


    @Override
    public boolean add(E e) {
        validValue(e);

        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().plus(e);

        if (e instanceof Bean) {
            ((Bean) e).setLogRoot(getRoot());
        }

        if (oldData != newData) {
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        for (E e : oldData) {
            if (e.equals(o) && e instanceof Bean) {
                ((Bean) e).setLogRoot(null);
                break;
            }
        }
        PVector<E> newData = log.getData().minus(o);

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

        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().plusAll(c);

        for (E e : c) {
            if (e instanceof Bean) {
                ((Bean) e).setLogRoot(getRoot());
            }
        }

        if (oldData != newData) {
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        for (E e : c) {
            validValue(e);
        }

        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().plusAll(index, c);

        for (E e : c) {
            if (e instanceof Bean) {
                ((Bean) e).setLogRoot(getRoot());
            }
        }

        if (oldData != newData) {
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().minusAll(c);

        if (oldData != newData) {
            for (E e : oldData) {
                if (!newData.contains(e) && e instanceof Bean) {
                    ((Bean) e).setLogRoot(null);
                }
            }
            log.setData(newData);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        ListLog<E> log = getOrAddLog();
        if (log.getData().isEmpty()) {
            return;
        }
        setChildrenLogRoot(null);
        log.setData(Empty.vector());
    }

    @Override
    public E get(int index) {
        return getData().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        validValue(element);

        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().with(index, element);

        if (oldData != newData) {
            log.setData(newData);
        }

        if (element instanceof Bean) {
            ((Bean) element).setLogRoot(getRoot());
        }

        E oldElement = oldData.get(index);
        if (oldElement instanceof Bean) {
            ((Bean) oldElement).setLogRoot(null);
        }

        return oldElement;
    }

    @Override
    public void add(int index, E element) {
        validValue(element);

        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().plus(index, element);

        if (oldData != newData) {
            log.setData(newData);
        }

        if (element instanceof Bean) {
            ((Bean) element).setLogRoot(getRoot());
        }

        E oldElement = oldData.get(index);
        if (oldElement instanceof Bean) {
            ((Bean) oldElement).setLogRoot(null);
        }
    }

    @Override
    public E remove(int index) {
        ListLog<E> log = getOrAddLog();

        PVector<E> oldData = log.getData();
        PVector<E> newData = log.getData().minus(index);

        if (oldData != newData) {
            log.setData(newData);
        }

        E oldElement = oldData.get(index);
        if (oldElement instanceof Bean) {
            ((Bean) oldElement).setLogRoot(null);
        }

        return oldElement;
    }

    @Override
    public int indexOf(Object o) {
        return getData().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getData().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return getData().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return getData().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getData().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return getData().toString();
    }
}
