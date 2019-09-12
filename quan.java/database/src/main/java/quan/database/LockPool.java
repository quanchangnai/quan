package quan.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁池，会默认创建一批锁
 * 使用之前才能设置大小，
 * 设置大小和获取锁不能并发执行
 * Created by quanchangnai on 2019/6/29.
 */
public class LockPool {

    private static int size = 1 << 16;

    private static List<Lock> locks = new ArrayList<>();

    private volatile static boolean used = false;

    static {
        for (int i = 0; i < size; i++) {
            locks.add(new ReentrantLock());
        }
    }

    public static void setSize(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("参数size必须大于0");
        }

        if (used) {
            //这里简单校验一下，不能百分之百保证安全
            throw new IllegalStateException("锁池已经在使用了，不能修改大小");
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


    static Lock getLock(Table table, Object key) {
        used = true;
        return getLock(getLockIndex(table, key));
    }

    static Lock getLock(int lockIndex) {
        used = true;
        return locks.get(lockIndex);
    }


    static int getLockIndex(Table table, Object key) {
        used = true;
        int hash = Objects.hash(table, key);
        return (hash & 0x7FFFFFFF) % locks.size();
    }

}
