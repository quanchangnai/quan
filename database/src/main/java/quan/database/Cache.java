package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.CallerUtil;
import quan.database.exception.DbException;
import quan.database.log.DataLog;
import quan.database.log.VersionLog;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class Cache<K, V extends Data<K>> implements Comparable<Cache<K, V>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final AtomicInteger nextId = new AtomicInteger();

    /**
     * 自增长的ID
     */
    private int id = nextId.incrementAndGet();

    private String name;

    /**
     * 缓存大小，缓存的数据行数超过此值时，要把过期的缓存清除
     */
    private int cacheSize;

    /**
     * 缓存过期时间(秒)
     */
    private int cacheExpire;

    private Database database;

    private Supplier<V> dataFactory;

    /**
     * 缓存的所有数据行
     */
    private Map<K, V> rows;

    private Set<K> inserts;

    private Set<K> updates;

    private Set<K> deletes;

    /**
     * 表级锁
     */
    private Lock lock = new ReentrantLock();


    public Cache(String name, Supplier<V> dataFactory) {
        this.name = name;
        this.dataFactory = dataFactory;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Database getDatabase() {
        return database;
    }

    public Supplier<V> getDataFactory() {
        return dataFactory;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    void init(Database database) {
        if (this.database != null) {
            return;
        }
        this.database = database;
        cacheSize = database.getCacheSize();
        cacheExpire = database.getCacheExpire();

        rows = new HashMap<>(cacheSize);
        inserts = new HashSet<>();
        updates = new HashSet<>();
        deletes = new HashSet<>();
    }

    @Override
    public int compareTo(Cache<K, V> other) {
        return this.id - other.id;
    }


    public Lock getLock() {
        CallerUtil.validCallerClass(Transaction.class);
        return lock;
    }

    public V getNoLock(K key) {
        CallerUtil.validCallerClass(DataLog.class);
        return rows.get(key);
    }

    private V getOnLock(K key) {
        try {
            lock.lock();
            return rows.get(key);
        } finally {
            lock.unlock();
        }
    }

    public void setInsert(V data) {
        CallerUtil.validCallerClass(DataLog.class);

        rows.put(data.getKey(), data);
        inserts.add(data.getKey());
        deletes.remove(data.getKey());
        updates.remove(data.getKey());
    }

    public void setUpdate(V data) {
        CallerUtil.validCallerClass(VersionLog.class);

        V value = rows.get(data.getKey());
        if (value == null || value != data) {
            //数据已被删除或者不受缓存管理
            return;
        }

        if (inserts.contains(data.getKey())) {
            //新插入的数据无需记录更新
            return;
        }

        updates.add(data.getKey());
    }

    public void setDelete(K key) {
        CallerUtil.validCallerClass(DataLog.class);

        rows.remove(key);
        if (inserts.remove(key)) {
            //新插入的数据被删除时清除插入记录就行
            return;
        }

        deletes.add(key);
        updates.remove(key);
    }

    public V get(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getCurrent();
        }

        V data;

        try {
            lock.lock();

            data = rows.get(key);
            if (data == null) {
                data = database.get(this, key);
                if (data != null) {
                    rows.put(key, data);
                }
            }

        } finally {
            lock.unlock();
        }

        log = new DataLog(data, data, this, key);
        transaction.addDataLog(log);

        return (V) log.getCurrent();

    }

    public void delete(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log == null) {
            log = new DataLog(null, getOnLock(key), this, key);
            transaction.addDataLog(log);
            return;
        }

        log.setCurrent(null);
    }

    public void insert(V data) {
        Objects.requireNonNull(data, "数据不能为空");
        Objects.requireNonNull(data.getKey(), "主键不能为空");

        if (get(data.getKey()) != null) {
            throw new DbException("数据已存在");
        }

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, data.getKey()));
        log.setCurrent(data);
    }

    /**
     * 存档数据，并且当缓存数据数量达到上限时清理过期缓存
     */
    public void store() {
        lock.lock();
        try {
            Set<V> puts = new HashSet<>();
            Set<K> deletes = new HashSet<>();

            for (K key : inserts) {
                V data = rows.get(key);
                puts.add(data);
            }

            for (K key : updates) {
                V data = rows.get(key);
                puts.add(data);
            }

            deletes.addAll(this.deletes);

            logger.debug("[{}]存档,inserts:{},updates:{},deletes:{}", name, inserts, updates, deletes);

            this.inserts.clear();
            this.updates.clear();
            this.deletes.clear();

            for (V data : puts) {
                database.put(data);
            }

            for (K key : deletes) {
                database.delete(this, key);
            }

            if (rows.size() > cacheSize) {
                checkExpireAndClear();
            }

        } finally {
            lock.unlock();
        }

    }

    /**
     * 检测过期缓存并清理
     */
    private void checkExpireAndClear() {
        long now = System.currentTimeMillis();
        Set<K> removes = new HashSet<>();

        Iterator<V> iterator = rows.values().iterator();
        while (iterator.hasNext()) {
            V data = iterator.next();
            if (now - data.getTouchTime() > cacheExpire * 1000) {
                iterator.remove();
                removes.add(data.getKey());
            }
        }

        logger.debug("[{}]过期缓存清除，rows:{},removes:{}", name, rows.size(), removes);
    }

}
