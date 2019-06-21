package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public class TableCache<K, V extends Data<K>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键名
     */
    private String primaryKeyName;

    /**
     * 缓存最大数量
     */
    private int cacheSize;

    /**
     * 缓存过期秒数
     */
    private int cacheExpire;

    private Database database;

    public TableCache(String tableName, String primaryKeyName) {
        this.tableName = tableName;
        this.primaryKeyName = primaryKeyName;
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
