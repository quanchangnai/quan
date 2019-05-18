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

    public final Lock getLock() {
        return lock;
    }

    @Override
    public final MappingData getRoot() {
        return this;
    }

    public final long getVersion() {
        return version;
    }

    public final void versionUp() {
        version++;
    }

    @Override
    public final int compareTo(MappingData o) {
        return this.lockIndex - o.lockIndex;
    }
}
