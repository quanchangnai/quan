package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.log.DataLog;
import quan.database.util.Validations;

import java.util.HashMap;
import java.util.Map;
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

    void init(Database database) {
        this.database = database;
        this.cacheSize = database.getCacheSize();
        this.cacheExpire = database.getCacheExpire();
        this.records = new HashMap<>(cacheSize);
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

    V createData(K key) {
        V data = dataFactory.get();
        data.setKey(key);
        return data;
    }


    public V get(K key) {
        Transaction transaction = Validations.validTransaction();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            if (log.isRemoved()) {
                return null;
            }
            return (V) log.getData();
        }

        V data = records.get(key);
        if (data == null) {
            data = database.get(name, key);

            if (data == null) {
                data = createData(key);
            }

            //缓存里没有数据要记录日志
            log = new DataLog(data, this, data.getKey());
            transaction.addDataLog(log);

        }

        return data;

    }

    public void remove(K key) {
        Transaction transaction = Validations.validTransaction();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log == null) {
            log = new DataLog(null, this, key);
        }

        log.setRemoved(true);

    }

    public void put(Data<K> data) {
        Transaction transaction = Validations.validTransaction();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, data.getKey()));
        if (log == null) {
            log = new DataLog(data, this, data.getKey());
            transaction.addDataLog(log);
        } else {
            log.setData(data);
            log.setRemoved(false);
        }

    }

}
