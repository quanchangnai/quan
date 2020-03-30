package quan.database;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 行数据
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data<K> extends Entity implements Comparable<Data<K>> {

    /**
     * 版本号，用于实现乐观锁
     */
    private long version;

    private volatile long touchTime = System.currentTimeMillis();

    private Table<K, ? extends Data<K>> table;

    private static final AtomicLong nextId = new AtomicLong();

    private long id = nextId.incrementAndGet();

    /**
     * 行级锁，当数据是纯内纯时使用，当数据需要持久化时使用公共锁池中的锁
     */
    private Lock lock;

    private volatile boolean expired;

    public Data(Table<K, ? extends Data<K>> table) {
        this.table = table;
        if (table == null) {
            lock = new ReentrantLock();
        }
    }

    final Lock _getLock() {
        return lock;
    }

    final Table<K, ? extends Data<K>> _getTable() {
        return table;
    }

    @Override
    protected final Data<K> _getRoot() {
        return this;
    }

    final long _getVersion() {
        return version;
    }

    final void versionUp() {
        version++;
        if (version < 0) {
            version = 1;
        }
    }

    final long touchTime() {
        return touchTime;
    }

    final void touch() {
        touchTime = System.currentTimeMillis();
    }

    final boolean _isExpired() {
        return expired;
    }

    final void _setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public final int compareTo(Data<K> other) {
        long compare = this.id - other.id;
        if (compare > 0) {
            return 1;
        } else if (compare == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public abstract K getKey();

}
