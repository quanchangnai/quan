package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

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

    /**
     * 存档线程数量
     */
    private int storeThreadNum = Runtime.getRuntime().availableProcessors();

    private List<StoreThread> storeThreads = new ArrayList<>();

    private int storeThreadIndex;

    public Database() {
        if (instance != null) {
            throw new IllegalStateException("只能打开一个数据库实例");
        }
        instance = this;

        startStoreThread();
    }

    public Database(int cacheSize, int cacheExpire, int storePeriod) {
        if (instance != null) {
            throw new IllegalStateException("只能打开一个数据库实例");
        }
        instance = this;

        this.cacheSize = cacheSize;
        this.cacheExpire = cacheExpire;
        this.storePeriod = storePeriod;

        startStoreThread();
    }

    public static Database getInstance() {
        return instance;
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
        if (cache.getDatabase() != null) {
            return;
        }
        cache.init(this);

        StoreThread storeThread = storeThreads.get(storeThreadIndex);
        storeThread.caches.add(cache);

        storeThreadIndex++;
        if (storeThreadIndex == storeThreads.size() - 1) {
            storeThreadIndex = 0;
        }

    }

    private void startStoreThread() {
        for (int i = 0; i < storeThreadNum; i++) {
            StoreThread storeThread = new StoreThread(storePeriod);
            storeThreads.add(storeThread);
            storeThread.start();
        }
    }

    public void close() {
        instance = null;
        for (StoreThread storeThread : storeThreads) {
            storeThread.running = false;
        }
    }

    protected abstract <K, V extends Data<K>> V get(Cache<K, V> cache, K key);


    protected abstract <K, V extends Data<K>> void put(V data);


    protected abstract <K, V extends Data<K>> void delete(Cache<K, V> cache, K key);


    private static class StoreThread extends Thread {

        private volatile boolean running = true;

        private int storePeriod;

        private List<Cache> caches = new ArrayList<>();

        public StoreThread(int storePeriod) {
            this.storePeriod = storePeriod;
        }

        public void registerCache(Cache cache) {
            caches.add(cache);
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(storePeriod * 1000);
                } catch (InterruptedException e) {
                }

                for (Cache cache : caches) {
                    cache.store();
                }
            }
        }

    }

}
