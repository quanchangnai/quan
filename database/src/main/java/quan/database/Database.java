package quan.database;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Cache> caches = new HashMap<>();

    public Database() {
    }

    public Database(int cacheSize, int cacheExpire) {
        this.cacheSize = cacheSize;
        this.cacheExpire = cacheExpire;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    public Map<String, Cache> getCaches() {
        return caches;
    }


    public void registerCaches(List<Cache> caches) {
        for (Cache cache : caches) {
            cache.init(this);
            this.caches.put(cache.getName(), cache);
        }
    }

    public abstract void open();

    public abstract void close();

    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        String json = doGet(cache, key.toString());
        if (json == null) {
            return null;
        }

        V data = cache.getDataFactory().get();

        data.decode(JSON.parseObject(json));
        return data;
    }

    protected abstract <K, V extends Data<K>> String doGet(Cache<K, V> cache, String key);


    protected abstract <K, V extends Data<K>> void put(V data);


    protected abstract <K, V extends Data<K>> void delete(Cache<K, V> cache, K key);

}
