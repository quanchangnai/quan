package quan.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by quanchangnai on 2019/6/29.
 */
public class LockPool {

    private static int size = 10000;

    private static List<ReadWriteLock> locks = new CopyOnWriteArrayList<>();

    static {
        for (int i = 0; i < size; i++) {
            locks.add(new ReentrantReadWriteLock());
        }
    }

    public synchronized static void setSize(int size) {
        if (size < 1) {
            return;
        }
        int changeSize = size - LockPool.size;
        if (changeSize > 0) {
            List<ReadWriteLock> addLocks = new ArrayList<>();
            for (int i = 0; i < changeSize; i++) {
                addLocks.add(new ReentrantReadWriteLock());
            }
            locks.addAll(addLocks);
        }
        if (changeSize < 0) {
            List<ReadWriteLock> removeLocks = locks.subList(size, LockPool.size);
            locks.removeAll(removeLocks);
        }
        LockPool.size = size;
    }

    public static int getSize() {
        return size;
    }


    public static ReadWriteLock getLock(Cache cache, Object key) {
        return locks.get(getLockIndex(cache, key));
    }

    public static ReadWriteLock getLock(Data data) {
        return locks.get(getLockIndex(data));
    }

    public static ReadWriteLock getLock(int lockIndex) {
        return locks.get(lockIndex);
    }


    public static int getLockIndex(Cache cache, Object key) {
        int hash = Objects.hash(cache, key) >>> 16;
        return hash % locks.size();
    }

    public static int getLockIndex(Data data) {
        int hash = Objects.hash(data) >>> 16;
        return hash % locks.size();
    }

}
