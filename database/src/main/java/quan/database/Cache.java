package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.log.DataLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class Cache<K, V extends Data<K>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private Supplier<V> dataFactory;

    /**
     * 缓存大小
     */
    private int cacheSize;

    /**
     * 缓存过期秒数
     */
    private int cacheExpire;

    private Database database;

    /**
     * 缓存记录
     */
    private Map<K, V> records;

    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public Cache(String name, Supplier<V> dataFactory) {
        this.name = name;
        this.dataFactory = dataFactory;
    }

    public String getName() {
        return name;
    }

    public Database getDatabase() {
        return database;
    }

    Supplier<V> getDataFactory() {
        return dataFactory;
    }

    void init(Database database) {
        this.database = database;
        this.cacheSize = database.getCacheSize();
        this.cacheExpire = database.getCacheExpire();
        this.records = new HashMap<>(cacheSize);
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public V getRecord(K key) {
        return records.get(key);
    }

    public void putRecord(K key, V data) {
        if (data == null) {
            records.remove(key);
        } else {
            records.put(key, data);
        }
    }

    public V get(K key) {
        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getData();
        }

        V data = records.get(key);
        if (data == null) {
            data = database.get(this, key);
            if (data != null) {
                records.put(key, data);
            }
        }

        return data;

    }

    public void delete(K key) {
        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log == null) {
            log = new DataLog(null, this, key);
            transaction.addDataLog(log);
        } else {
            log.setData(null);
        }

    }

    public void insert(V data) {
        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, data.getKey()));
        if (log != null) {

        }

    }

}
