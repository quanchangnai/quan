package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认数据库实例
     */
    private static Database instance;

    private static AtomicInteger nextId = new AtomicInteger();

    /**
     * 数据库实例ID，正常情况下只会有一个数据库实例
     */
    private int id;

    /**
     * 数据库配置
     */
    private Config config;

    /**
     * 管理的所有缓存表
     */
    private Map<String, Table> tables = new HashMap<>();

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

        open0();
    }

    protected abstract void open0();

    public static Database getDefault() {
        return instance;
    }

    public static void setDefault(Database database) {
        Database.instance = database;
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

    public synchronized void registerTable(Table table) {
        if (tables.containsKey(table.getName())) {
            throw new IllegalStateException("缓存表已经被注册了");
        }
        if (table.isWorkable()) {
            throw new IllegalStateException("缓存表已经被注册到其他数据库");
        }
        table.init(this);

        tables.put(table.getName(), table);

        storeThreads.get(storeThreadIndex).tables.add(table);

        storeThreadIndex++;
        if (storeThreadIndex == storeThreads.size() - 1) {
            storeThreadIndex = 0;
        }

        registerTable0(table);
    }

    protected abstract void registerTable0(Table table);


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
        closed = true;

        tables.clear();
        for (StoreThread storeThread : storeThreads) {
            storeThread.close();
        }
        storeThreads.clear();

        close0();
        logger.debug("数据库[{}]已关闭", getName());
    }

    protected abstract void close0();

    protected abstract <K, V extends Data<K>> V get(Table<K, V> table, K key);


    protected abstract <K, V extends Data<K>> void put(V data);


    protected abstract <K, V extends Data<K>> void delete(Table<K, V> table, K key);

    protected <K, V extends Data<K>> void bulkWrite(Table<K, V> table, Set<V> puts, Set<K> deletes) {
        checkClosed();
        for (V data : puts) {
            put(data);
        }
        for (K key : deletes) {
            delete(table, key);
        }
    }

    private static class StoreThread extends Thread {

        private volatile boolean running;

        private int storePeriod;

        private List<Table> tables = new CopyOnWriteArrayList<>();

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
                    } catch (InterruptedException ignored) {
                    }
                }

                if (!running) {
                    break;
                }

                for (Table table : tables) {
                    table.store();
                }
            }
            tables.clear();
        }

        void close() {
            running = false;
            for (Table table : tables) {
                table.finalStore();
            }
        }
    }

    public abstract static class Config {

        /**
         * 数据库实例名字
         */
        private String name = "";

        /**
         * 表缓存过期时间(秒)
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
            this.storePeriod = Math.max(1, storePeriod);
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
