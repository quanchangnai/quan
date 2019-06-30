package quan.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
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

    private static List<Lock> locks = new ArrayList<>();

    private volatile static boolean used = false;

    static {
        for (int i = 0; i < size; i++) {
            locks.add(new ReentrantLock());
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
            List<Lock> addLocks = new ArrayList<>();
            for (int i = 0; i < changeSize; i++) {
                addLocks.add(new ReentrantLock());
            }
            locks.addAll(addLocks);
        }
        if (changeSize < 0) {
            List<Lock> removeLocks = locks.subList(size, LockPool.size);
            locks.removeAll(removeLocks);
        }
        LockPool.size = size;
    }

    public static int getSize() {
        return size;
    }


    static Lock getLock(Cache cache, Object key) {
        used = true;
        return getLock(getLockIndex(cache, key));
    }

    static Lock getLock(int lockIndex) {
        used = true;
        return locks.get(lockIndex);
    }


    static int getLockIndex(Cache cache, Object key) {
        used = true;
        int hashCode = Objects.hash(cache, key);
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        return hashCode % locks.size();
    }

}
