package quan.database.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.database.Bean;
import quan.database.Data;
import quan.database.Node;
import quan.database.Transaction;
import quan.database.log.FieldLog;
import quan.database.Validations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by quanchangnai on 2019/5/21.
 */
public final class ListField<E> extends Node implements List<E>, Field<PVector<E>> {

    private PVector<E> data = Empty.vector();

    public ListField(Data root) {
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
    public void setValue(PVector<E> data) {
        this.data = data;
    }

    @Override
    public PVector<E> getValue() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            FieldLog<PVector<E>> log = (FieldLog<PVector<E>>) transaction.getFieldLog(this);
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

    private FieldLog<PVector<E>> getOrAddLog() {
        Transaction transaction = Transaction.get();

        Data root = getRoot();
        if (root != null) {
            transaction.addVersionLog(root);
        }

        FieldLog<PVector<E>> log = (FieldLog<PVector<E>>) transaction.getFieldLog(this);
        if (log == null) {
            log = new FieldLog<>(this, data);
            transaction.addFieldLog(log);
        }
        return log;
    }


    @Override
    public boolean add(E e) {
        Validations.validCollectionValue(e);

        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().plus(e);

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
        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        for (E e : oldData) {
            if (e.equals(o) && e instanceof Bean) {
                ((Bean) e).setLogRoot(null);
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
            Validations.validCollectionValue(e);
        }

        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().plusAll(c);

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
    public boolean addAll(int index, Collection<? extends E> c) {
        for (E e : c) {
            Validations.validCollectionValue(e);
        }

        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().plusAll(index, c);

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
    public boolean removeAll(Collection<?> c) {
        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().minusAll(c);

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
        FieldLog<PVector<E>> log = getOrAddLog();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        Validations.validCollectionValue(element);

        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().with(index, element);

        if (oldData != newData) {
            log.setValue(newData);
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
        Validations.validCollectionValue(element);

        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().plus(index, element);

        if (oldData != newData) {
            log.setValue(newData);
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
        FieldLog<PVector<E>> log = getOrAddLog();

        PVector<E> oldData = log.getValue();
        PVector<E> newData = log.getValue().minus(index);

        if (oldData != newData) {
            log.setValue(newData);
        }

        E oldElement = oldData.get(index);
        if (oldElement instanceof Bean) {
            ((Bean) oldElement).setLogRoot(null);
        }

        return oldElement;
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
