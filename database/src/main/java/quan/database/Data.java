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

    private static final AtomicInteger lockIndexGenerator = new AtomicInteger();

    private int lockIndex = lockIndexGenerator.incrementAndGet();

    /**
     * 行级锁
     */
    private Lock lock = new ReentrantLock();

    private volatile long touchTime = System.currentTimeMillis();


    public final Lock getLock() {
        return lock;
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

    @Override
    public final int compareTo(Data<K> other) {
        return this.lockIndex - other.lockIndex;
    }

    public abstract K getKey();

    public abstract void setKey(K key);

    public abstract Cache<K, ? extends Data<K>> getCache();

}
