package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class Cache<K, V extends Data<K>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private String primaryKeyName;

    private int cacheSize;

    /**
     * 缓存过期秒数
     */
    private int cacheExpire;

    private Database database;

    private ConcurrentHashMap<K, V> records = new ConcurrentHashMap<>(cacheSize);


    public Cache(String name, String primaryKeyName) {
        this.name = name;
        this.primaryKeyName = primaryKeyName;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public Database getDatabase() {
        return database;
    }

    void setDatabase(Database database) {
        this.database = database;
        this.cacheSize = database.getCacheSize();
        this.cacheExpire = database.getCacheExpire();
    }

    public V get(K key) {
        return null;
    }

    public void remove(K key) {

    }

    public void put(V data) {

    }

}
