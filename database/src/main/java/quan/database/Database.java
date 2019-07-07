package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认数据库实例
     */
    private static Database instance;

    private AtomicInteger nextId = new AtomicInteger();

    /**
     * 数据库实例ID，正常情况下只会有一个数据库实例
     */
    private int id;

    /**
     * 数据库配置
     */
    private Config config;

    /**
     * 管理的所有缓存
     */
    private Map<String, Cache> caches = new HashMap<>();

    /**
     * 存档线程数量
     */
    private int storeThreadNum = Runtime.getRuntime().availableProcessors();

    /**
     * 所有的存档线程
     */
    private List<StoreThread> storeThreads = new ArrayList<>();

    private int storeThreadIndex;

    private volatile boolean closed;

    private Thread shutdownHookThread;


    public Database(Config config) {
        //默认数据库实例为空时自动设置，不为空时需要手动更改
        if (instance == null) {
            instance = this;
        }

        this.id = nextId.incrementAndGet();
        this.config = config;

        open();
    }

    private void open() {
        for (int i = 0; i < storeThreadNum; i++) {
            StoreThread storeThread = new StoreThread(config.storePeriod);
            storeThread.setName(getName() + "-store-thread-" + (i + 1));
            storeThreads.add(storeThread);
            storeThread.start();
        }

        shutdownHookThread = new Thread(instance::close, getName() + "-shutdown-hook-thread");
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);

        open0();
    }

    protected abstract void open0();

    public static Database getDefault() {
        return instance;
    }

    public static void setDefault(Database database) {
        Database.instance = instance;
    }

    public Config getConfig() {
        return config;
    }

    public String getName() {
        String name = config.getName();
        if (name.equals("")) {
            name = "database-" + id;
        }
        return name;
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
        if (closed && Thread.currentThread() == shutdownHookThread) {
            return;
        }
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

    protected <K, V extends Data<K>> void putAndDelete(Cache<K, V> cache, Set<V> puts, Set<K> deletes) {
        for (V data : puts) {
            put(data);
        }
        for (K key : deletes) {
            delete(cache, key);
        }
    }

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
                for (int i = 0; i < storePeriod; i++) {
                    if (!running) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
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

    public abstract static class Config {

        /**
         * 数据库实例名字
         */
        private String name = "";

        /**
         * 缓存大小
         */
        private int cacheSize = 2000;

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

        public String getName() {
            return name == null ? "" : name.trim();
        }

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public int getCacheSize() {
            return cacheSize;
        }

        public Config setCacheSize(int cacheSize) {
            this.cacheSize = Math.max(100, cacheSize);
            return this;
        }

        public int getCacheExpire() {
            return cacheExpire;
        }

        public Config setCacheExpire(int cacheExpire) {
            this.cacheExpire = Math.max(60, cacheExpire);
            return this;
        }

        public int getStorePeriod() {
            return storePeriod;
        }

        public Config setStorePeriod(int storePeriod) {
            this.storePeriod = Math.max(60, storePeriod);
            return this;
        }

        public int getStoreThreadNum() {
            return storeThreadNum;
        }

        public Config setStoreThreadNum(int storeThreadNum) {
            this.storeThreadNum = Math.max(1, storeThreadNum);
            return this;
        }
    }

}
