package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 缓存记录最大数量
     */
    private int cacheSize = 5000;

    /**
     * 缓存过期时间秒
     */
    private int cacheExpire = 600;

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }


    public abstract void open();

    public abstract void close();

    protected abstract <K, V extends Data<K>> V get(Cache<K, V> cache, K key);

    protected abstract <K, V extends Data<K>> V remove(Cache<K, V> cache, K key);

    protected abstract <K, V extends Data<K>> V put(Cache<K, V> cache, V data);

}
