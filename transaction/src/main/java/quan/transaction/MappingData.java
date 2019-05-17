package quan.transaction;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class MappingData extends Data implements Comparable<MappingData> {

    /**
     * 版本号，用于实现乐观锁
     */
    private volatile long version;

    private static final AtomicInteger lockIndexGenerator = new AtomicInteger();

    private int lockIndex;

    private Lock lock = new ReentrantLock();

    {
        lockIndex = lockIndexGenerator.incrementAndGet();
    }

    public Lock getLock() {
        return lock;
    }

    public long getLockIndex() {
        return lockIndex;
    }

    @Override
    public MappingData getRoot() {
        return this;
    }

    public long getVersion() {
        return version;
    }

    public void versionUp() {
        version++;
    }

    @Override
    public int compareTo(MappingData o) {
        return this.lockIndex - o.lockIndex;
    }
}
