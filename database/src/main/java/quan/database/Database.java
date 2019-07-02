package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认数据库实例
     */
    private static Database instance;

    /**
     * 缓存大小
     */
    private int cacheSize = 5000;

    /**
     * 缓存过期时间(秒)
     */
    private int cacheExpire = 600;

    /***
     * 存档间隔(秒)
     */
    private int storePeriod = 10;

    private Map<String, Cache> caches = new HashMap<>();

    /**
     * 存档线程数量
     */
    private int storeThreadNum = Runtime.getRuntime().availableProcessors();

    private List<StoreThread> storeThreads = new ArrayList<>();

    private int storeThreadIndex;

    private volatile boolean closed;

    public Database() {
        //默认数据库实例为空时自动设置，不为空时需要手动更改
        if (instance == null) {
            instance = this;
        }

        startStoreThread();
    }

    /**
     * 参数设置小于0时采用默认值
     *
     * @param cacheSize
     * @param cacheExpire
     * @param storePeriod
     * @param storeThreadNum
     */
    public Database(int cacheSize, int cacheExpire, int storePeriod, int storeThreadNum) {
        if (instance == null) {
            instance = this;
        }
        if (cacheSize > 0) {
            this.cacheSize = cacheSize;
        }
        if (cacheExpire > 0) {
            this.cacheExpire = cacheExpire;
        }
        if (storePeriod > 0) {
            this.storePeriod = storePeriod;
        }
        if (storeThreadNum > 0) {
            this.storeThreadIndex = storeThreadNum;
        }

        startStoreThread();
    }

    public static Database getDefault() {
        return instance;
    }

    public static void setDefault(Database database) {
        Database.instance = instance;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    public int getStorePeriod() {
        return storePeriod;
    }

    public Database setStoreThreadNum(int storeThreadNum) {
        if (storeThreadNum < 1) {
            return this;
        }
        this.storeThreadNum = storeThreadNum;
        return this;
    }

    public synchronized void registerCache(Cache cache) {
        if (caches.containsKey(cache.getName())) {
            throw new IllegalStateException("缓存已经被注册了");
        }
        if (cache.getDatabase() != null) {
            throw new IllegalStateException("缓存已经被注册到其他数据库");
        }
        cache.init(this);

        caches.put(cache.getName(), cache);

        storeThreads.get(storeThreadIndex).caches.add(cache);

        storeThreadIndex++;
        if (storeThreadIndex == storeThreads.size() - 1) {
            storeThreadIndex = 0;
        }

        registerCache0(cache);
    }

    protected abstract void registerCache0(Cache cache);


    private void startStoreThread() {
        for (int i = 0; i < storeThreadNum; i++) {
            StoreThread storeThread = new StoreThread(storePeriod);
            storeThreads.add(storeThread);
            storeThread.start();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    protected void checkClosed() {
        if (closed) {
            throw new IllegalStateException("数据库已关闭");
        }
    }

    /**
     * 关闭数据库时会存档后清空缓存，但未结束的事务会执行失败
     */
    public synchronized void close() {
        checkClosed();
        if (instance == this) {
            instance = null;
        }
        caches.clear();
        for (StoreThread storeThread : storeThreads) {
            storeThread.close();
        }
        storeThreads.clear();

        close0();

        closed = true;
    }

    protected abstract void close0();

    protected abstract <K, V extends Data<K>> V get(Cache<K, V> cache, K key);


    protected abstract <K, V extends Data<K>> void put(V data);


    protected abstract <K, V extends Data<K>> void delete(Cache<K, V> cache, K key);


    private static class StoreThread extends Thread {

        private volatile boolean running;

        private int storePeriod;

        private List<Cache> caches = new CopyOnWriteArrayList<>();

        public StoreThread(int storePeriod) {
            this.storePeriod = storePeriod;
        }

        @Override
        public void run() {
            running = true;

            while (running) {
                long sleepTime = 0;
                while (sleepTime < storePeriod * 1000) {
                    try {
                        sleepTime = sleepTime + 1;
                        Thread.sleep(1);
                        if (!running) {
                            break;
                        }
                    } catch (InterruptedException e) {
                    }
                }

                if (!running) {
                    break;
                }

                for (Cache cache : caches) {
                    cache.store();
                }
            }
            caches.clear();
        }

        void close() {
            running = false;
            for (Cache cache : caches) {
                cache.close();
            }
        }
    }

}
