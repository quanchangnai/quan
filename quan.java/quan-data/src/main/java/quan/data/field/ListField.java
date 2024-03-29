package quan.data.field;

import org.pcollections.Empty;
import org.pcollections.PVector;
import quan.data.*;

import java.util.*;

/**
 * Created by quanchangnai on 2019/5/21.
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public final class ListField<E> extends Node implements List<E>, Field {

    private PVector<E> origin = Empty.vector();

    private Delegate<E> delegate = new Delegate<>(this);

    private int modCount;

    public ListField(Data<?> owner, int position) {
        _setOwner(owner, position, false);
    }

    public List<E> getDelegate() {
        return delegate;
    }

    @Override
    public void commit(Object log) {
        Log<E> log1 = (Log<E>) log;
        origin = log1.list;
        modCount = log1.modCount;
    }

    @Override
    public void _setChildrenLogOwner(Data<?> owner, int position) {
        for (E e : getCurrent()) {
            if (e instanceof Bean) {
                _setLogOwner((Bean) e, owner, position);
            }
        }
    }

    private Log<E> getLog(Transaction transaction, boolean write) {
        if (transaction == null) {
            return null;
        }

        Log<E> log = (Log<E>) _getFieldLog(transaction, this);
        if (write && log == null) {
            log = new Log<>(origin, modCount);
            _setFieldLog(transaction, this, log, _getLogOwner(transaction), _getLogPosition(transaction));
        }

        return log;
    }

    public PVector<E> getCurrent() {
        return getCurrent(Transaction.get());
    }

    public PVector<E> getCurrent(Transaction transaction) {
        Log<E> log = getLog(transaction, false);
        if (log != null) {
            return log.list;
        }
        return origin;
    }

    @Override
    public int size() {
        return getCurrent().size();
    }

    @Override
    public boolean isEmpty() {
        return getCurrent().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getCurrent().contains(o);
    }

    private class IteratorImpl implements Iterator<E> {

        //提前取出，避免遍历过程中频繁调用
        Transaction transaction = Transaction.get();

        int cursor;

        int last = -1;

        int expectedModCount = getModCount();

        Log<E> log;

        PVector<E> list = log != null ? log.list : ListField.this.origin;

        Iterator<E> iterator;

        public IteratorImpl() {
            //如果迭代过程中没有修改，用这个比较快
            this.iterator = list.iterator();
        }

        @Override
        public boolean hasNext() {
            return cursor != this.list.size();
        }

        @Override
        public E next() {
            checkConcurrentModification();
            try {
                E next;
                if (iterator != null) {
                    next = iterator.next();
                } else {
                    next = this.list.get(cursor);
                }
                last = cursor;
                cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkConcurrentModification();
                throw new NoSuchElementException();
            }

        }

        @Override
        public void remove() {
            if (last < 0) {
                throw new IllegalStateException();
            }
            checkConcurrentModification();

            try {
                ListField.this.remove(last);
                if (last < cursor) {
                    cursor--;
                }
                last--;
                expectedModCount = getModCount();
                this.list = log.list;
                iterator = null;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }

        }

        int getModCount() {
            if (transaction != null && log == null) {
                log = getLog(transaction, false);
            }
            if (log != null) {
                return log.modCount;
            }
            return ListField.this.modCount;
        }

        void checkConcurrentModification() {
            if (getModCount() != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorImpl();
    }

    @Override
    public Object[] toArray() {
        return getCurrent().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getCurrent().toArray(a);
    }

    @Override
    public boolean add(E e) {
        Validations.validateCollectionValue(e);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            log.modCount++;
            log.list = log.list.plus(e);
            if (e instanceof Bean) {
                _setLogOwner((Bean) e, _getLogOwner(transaction), _getLogPosition(transaction));
            }
        } else if (Transaction.isOptional()) {
            return plus(e);
        } else {
            Validations.transactionError();
        }

        return true;
    }

    public boolean plus(E e) {
        Validations.validateCollectionValue(e);

        modCount++;
        origin = origin.plus(e);
        if (e instanceof Bean) {
            _setOwner((Bean) e, _getOwner(), _getPosition());
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            PVector<E> oldList = log.list;
            log.modCount++;
            log.list = oldList.minus(o);

            if (oldList != log.list) {
                if (o instanceof Bean) {
                    _setLogOwner((Bean) o, null, 0);
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = origin;
            modCount++;
            origin = oldList.minus(o);

            if (oldList != origin) {
                if (o instanceof Bean) {
                    _setOwner((Bean) o, null, 0);
                }
                return true;
            }
        } else {
            Validations.transactionError();
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(getCurrent()).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        c.forEach(Validations::validateCollectionValue);

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            PVector<E> oldList = log.list;
            log.list = oldList.plusAll(index, c);
            log.modCount++;

            if (oldList != log.list) {
                Data<?> owner = _getLogOwner(transaction);
                int position = _getLogPosition(transaction);
                for (E e : c) {
                    if (e instanceof Bean) {
                        _setLogOwner((Bean) e, owner, position);
                    }
                }
                return true;
            }
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = origin;
            modCount++;
            origin = oldList.plusAll(index, c);

            if (oldList != origin) {
                Data<?> owner = _getOwner();
                int position = _getPosition();
                for (E e : c) {
                    if (e instanceof Bean) {
                        _setOwner((Bean) e, owner, position);
                    }
                }
                return true;
            }
        } else {
            Validations.transactionError();
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
    public void clear() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            if (!log.list.isEmpty()) {
                log.modCount++;
                _setChildrenLogOwner(null, 0);
                log.list = Empty.vector();
            }
        } else if (!Transaction.isOptional()) {
            Validations.transactionError();
        } else if (!origin.isEmpty()) {
            modCount++;
            for (E e : origin) {
                if (e instanceof Bean) {
                    _setOwner((Bean) e, null, _getPosition());
                }
            }
            origin = Empty.vector();
        }
    }

    @Override
    public E get(int index) {
        return getCurrent().get(index);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);

        boolean modified = false;
        Iterator<E> iterator = iterator();

        while (iterator.hasNext()) {
            if (c.contains(iterator.next())) {
                continue;
            }
            iterator.remove();
            modified = true;
        }

        return modified;
    }

    @Override
    public E set(int index, E e) {
        Validations.validateCollectionValue(e);

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            PVector<E> oldList = log.list;
            log.modCount++;
            log.list = oldList.with(index, e);

            if (e instanceof Bean) {
                _setLogOwner((Bean) e, _getLogOwner(transaction), _getLogPosition(transaction));
            }
            E old = oldList.get(index);
            if (old instanceof Bean) {
                _setLogOwner((Bean) old, null, 0);
            }
            return old;
        } else if (Transaction.isOptional()) {
            PVector<E> oldList = origin;
            modCount++;
            origin = oldList.with(index, e);

            if (e instanceof Bean) {
                _setOwner((Bean) e, _getOwner(), _getPosition());
            }
            E old = oldList.get(index);
            if (old instanceof Bean) {
                _setOwner((Bean) old, null, 0);
            }
            return old;
        } else {
            Validations.transactionError();
            return null;
        }
    }

    @Override
    public void add(int index, E e) {
        Validations.validateCollectionValue(e);
        Transaction transaction = Transaction.get();

        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            log.modCount++;
            log.list = log.list.plus(index, e);
            if (e instanceof Bean) {
                _setLogOwner((Bean) e, _getLogOwner(transaction), _getLogPosition(transaction));
            }
        } else if (Transaction.isOptional()) {
            modCount++;
            origin = origin.plus(index, e);
            if (e instanceof Bean) {
                _setOwner((Bean) e, _getOwner(), _getPosition());
            }
        } else {
            Validations.transactionError();
        }
    }

    @Override
    public E remove(int index) {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Log<E> log = getLog(transaction, true);
            E e = log.list.get(index);
            log.modCount++;
            log.list = log.list.minus(index);

            if (e instanceof Bean) {
                _setLogOwner((Bean) e, null, 0);
            }

            return e;
        } else if (Transaction.isOptional()) {
            E e = origin.get(index);
            modCount++;
            origin = origin.minus(index);

            if (e instanceof Bean) {
                _setOwner((Bean) e, null, 0);
            }

            return e;
        } else {
            Validations.transactionError();
            return null;
        }
    }

    @Override
    public int indexOf(Object o) {
        return getCurrent().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getCurrent().lastIndexOf(o);
    }

    private class ListIteratorImpl extends IteratorImpl implements ListIterator<E> {

        public ListIteratorImpl(int index) {
            cursor = index;
            iterator = null;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public E previous() {
            checkConcurrentModification();
            try {
                E previous = this.list.get(cursor - 1);
                last = --cursor;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkConcurrentModification();
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(E e) {
            if (last < 0) {
                throw new IllegalStateException();
            }
            checkConcurrentModification();

            try {
                ListField.this.set(last, e);
                expectedModCount = getModCount();
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            checkConcurrentModification();

            try {
                ListField.this.add(cursor, e);
                last = -1;
                cursor++;
                expectedModCount = getModCount();
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIteratorImpl(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size());
        }
        return new ListIteratorImpl(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getCurrent().subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return String.valueOf(getCurrent());
    }

    private static class Log<E> {

        private PVector<E> list;

        private int modCount;

        public Log(PVector<E> list, int modCount) {
            this.list = list;
            this.modCount = modCount;
        }

    }

    @SuppressWarnings("NullableProblems")
    private static class Delegate<E> implements List<E> {

        private ListField<E> field;

        public Delegate(ListField<E> field) {
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
            return new HashSet<>(field).containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return field.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return field.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return field.removeAll(c);
        }

        @Override
        public void clear() {
            field.clear();
        }

        @Override
        public E get(int index) {
            return field.get(index);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return field.retainAll(c);
        }

        @Override
        public E set(int index, E e) {
            return field.set(index, e);
        }

        @Override
        public void add(int index, E e) {
            field.add(index, e);
        }

        @Override
        public E remove(int index) {
            return field.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return field.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return field.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            return field.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return field.listIterator(index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return field.subList(fromIndex, toIndex);
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }

}
