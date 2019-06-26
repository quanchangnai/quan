package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.CallerUtil;
import quan.database.exception.DbException;
import quan.database.log.DataLog;
import quan.database.log.VersionLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
     * 自增的ID
     */
    private int id = nextId.incrementAndGet();

    private String name;

    /**
     * 缓存大小
     */
    private int cacheSize;

    /**
     * 缓存过期秒数
     */
    private int cacheExpire;

    private Database database;

    private Supplier<V> dataFactory;

    /**
     * 缓存数据
     */
    private Map<K, V> all;

    private Set<K> inserts = new HashSet<>();

    private Set<K> updates = new HashSet<>();

    private Set<K> deletes = new HashSet<>();

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

    void init(Database database) {
        if (this.database != null) {
            return;
        }
        this.database = database;
        this.cacheSize = database.getCacheSize();
        this.cacheExpire = database.getCacheExpire();
        this.all = new HashMap<>(cacheSize);
        inserts = new HashSet<>();
        updates = new HashSet<>();
        deletes = new HashSet<>();

        //临时
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    store();
                }
            }
        }.start();
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
        return all.get(key);
    }

    private V getOnLock(K key) {
        try {
            lock.lock();
            return all.get(key);
        } finally {
            lock.unlock();
        }
    }


    public void setDelete(K key) {
        CallerUtil.validCallerClass(DataLog.class);

        all.remove(key);
        if (inserts.remove(key)) {
            //新插入的数据被删除时清除插入记录就行
            return;
        }

        deletes.add(key);
        updates.remove(key);
    }

    public void setInsert(V data) {
        CallerUtil.validCallerClass(DataLog.class);

        all.put(data.getKey(), data);
        inserts.add(data.getKey());
        deletes.remove(data.getKey());

    }

    public void setUpdate(V data) {
        CallerUtil.validCallerClass(VersionLog.class);

        V value = all.get(data.getKey());
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

    public V get(K key) {
        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getCurrent();
        }

        V data;

        try {
            lock.lock();

            data = all.get(key);
            if (data == null) {
                data = database.get(this, key);
                if (data != null) {
                    all.put(key, data);
                }
            }

        } finally {
            lock.unlock();
        }

        log = new DataLog(data, data, this, key);
        transaction.addDataLog(log);

        return data;

    }

    public void delete(K key) {
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
        if (get(data.getKey()) != null) {
            throw new DbException("数据已存在");
        }

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, data.getKey()));
        log.setCurrent(data);

    }

    /**
     * 存档数据
     */
    public void store() {
        Set<V> puts = new HashSet<>();
        Set<K> deletes = new HashSet<>();

        try {
            lock.lock();

            for (K key : inserts) {
                V data = all.get(key);
                puts.add(data);
            }


            for (K key : updates) {
                V data = all.get(key);
                puts.add(data);
            }

            deletes.addAll(this.deletes);

            logger.debug("缓存[{}]存档，插入：{}，更新：{}，删除：{}", name, inserts, updates, deletes);

            this.inserts.clear();
            this.updates.clear();
            this.deletes.clear();

        } finally {
            lock.unlock();
        }

        synchronized (this) {
            //定时存档的间隔很长，应该不需要加锁
            //这里加锁是为了防止上次存档还没完成下一次存档又开始了的极端情况
            for (V data : puts) {
                database.put(data);
            }

            for (K key : deletes) {
                database.delete(this, key);
            }
        }
    }

    public void expire(K key) {

    }

}
