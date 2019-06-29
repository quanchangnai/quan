package quan.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁池
 * 使用之前才能设置大小，
 * 可以不设置而采用默认大小,
 * 设置大小和获取锁不能并发执行
 * Created by quanchangnai on 2019/6/29.
 */
public class LockPool {

    private static int size = 10000;

    private static List<ReadWriteLock> locks = new ArrayList<>();

    private volatile static boolean used = false;

    static {
        for (int i = 0; i < size; i++) {
            locks.add(new ReentrantReadWriteLock());
        }
    }

    public static void setSize(int size) {
        if (used) {
            //这里不能保证安全，只是简单校验一下
            throw new IllegalStateException("锁池已经在使用了，不能修改大小");
        }
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


    static ReadWriteLock getLock(Cache cache, Object key) {
        used = true;
        return getLock(getLockIndex(cache, key));
    }

    static ReadWriteLock getLock(Data data) {
        used = true;
        return locks.get(getLockIndex(data));
    }

    static ReadWriteLock getLock(int lockIndex) {
        used = true;
        return locks.get(lockIndex);
    }


    static int getLockIndex(Cache cache, Object key) {
        used = true;
        return Objects.hash(cache, key) % locks.size();
    }

    static int getLockIndex(Data data) {
        used = true;
        return data.hashCode() % locks.size();
    }

}
