package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 缓存最大数量
     */
    private int cacheSize = 1000;

    /**
     * 缓存过期秒数
     */
    private int cacheExpire = 60;

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    public void open() {

    }

    public void close() {

    }

    protected abstract <K, V extends Data<K>> V get(TableCache<K, V> tableCache, K key);

    protected abstract <K, V extends Data<K>> V remove(TableCache<K, V> tableCache, K key);

    protected abstract <K, V extends Data<K>> V put(TableCache<K, V> tableCache, V data);

}
