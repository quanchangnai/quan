package quan.database;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data<K> extends Bean implements Comparable<Data<K>> {

    /**
     * 版本号，用于实现乐观锁
     */
    private volatile long version;

    private volatile long touchTime = System.currentTimeMillis();

    private Cache<K, ? extends Data<K>> cache;

    private static final AtomicInteger nextLockId = new AtomicInteger();

    private int lockId;

    /**
     * 行级锁，当数据不受缓存管理时使用，受缓存管理时使用锁池
     */
    private Lock lock;

    private volatile boolean expired;

    public Data(Cache<K, ? extends Data<K>> cache) {
        this.cache = cache;
        if (cache == null) {
            lock = new ReentrantLock();
            lockId = nextLockId.incrementAndGet();
        }
    }

    final Lock getLock() {
        return lock;
    }

    public final Cache<K, ? extends Data<K>> getCache() {
        return cache;
    }

    @Override
    public final Data<K> getRoot() {
        return this;
    }

    public final long getVersion() {
        return version;
    }

    public final void versionUp() {
        version++;
        if (version < 0) {
            version = 1;
        }
    }

    public final long getTouchTime() {
        return touchTime;
    }

    public final void touch() {
        touchTime = System.currentTimeMillis();
    }

    final boolean isExpired() {
        return expired;
    }

    final void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public final int compareTo(Data<K> other) {
        return this.lockId - other.lockId;
    }

    public abstract K getKey();


}
