package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.CallerUtil;
import quan.database.exception.DbException;
import quan.database.log.DataLog;
import quan.database.log.VersionLog;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
    private Map<K, Row<V>> rows;

    private Set<K> inserts;

    private Set<K> updates;

    private Set<K> deletes;

    /**
     * 表级锁，存档时加写锁，其他地方加读锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();


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

        rows = new ConcurrentHashMap<>(cacheSize);
        inserts = new HashSet<>();
        updates = new HashSet<>();
        deletes = new HashSet<>();
    }

    @Override
    public int compareTo(Cache<K, V> other) {
        return this.id - other.id;
    }


    public ReadWriteLock getLock() {
        CallerUtil.validCallerClass(Transaction.class);
        return lock;
    }

    public Row<V> getRow(K key) {
        CallerUtil.validCallerClass(DataLog.class);
        return rows.get(key);
    }

    public void setInsert(V data) {
        CallerUtil.validCallerClass(DataLog.class);
        rows.get(data.getKey()).state = Row.INSERT;
    }

    public void setUpdate(V data) {
        CallerUtil.validCallerClass(VersionLog.class);
        rows.get(data.getKey()).state = Row.UPDATE;
    }

    public void setDelete(K key) {
        CallerUtil.validCallerClass(DataLog.class);
        rows.get(key).state = Row.DELETE;
    }


    public V get(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getCurrent();
        }

        Row<V> row = null;
        V data = null;

        try {
            //存档时阻塞
            lock.readLock().lock();

            row = rows.get(key);
            if (row == null) {
                data = database.get(this, key);
                if (data != null) {
                    row = new Row<>(data);
                    Row<V> oldRow = rows.putIfAbsent(key, row);
                    if (oldRow != null) {
                        row = oldRow;
                    }
                }
            }
            if (row != null) {
                data = row.data;
            }
        } finally {
            lock.readLock().lock();
        }

        log = new DataLog(data, row, this, key);
        transaction.addDataLog(log);

        return (V) log.getCurrent();

    }

    public void delete(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log == null) {
            log = new DataLog(null, rows.get(key), this, key);
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
        lock.writeLock().lock();
        try {
            Set<V> puts = new HashSet<>();
            Set<K> deletes = new HashSet<>();


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
            lock.writeLock().lock();
        }

    }

    /**
     * 检测过期缓存并清理
     */
    private void checkExpireAndClear() {
        long now = System.currentTimeMillis();
        Set<K> removes = new HashSet<>();

//        Iterator<V> iterator = rows.values().iterator();
//        while (iterator.hasNext()) {
//            V data = iterator.next();
//            if (now - data.getTouchTime() > cacheExpire * 1000) {
//                iterator.remove();
//                removes.add(data.getKey());
//            }
//        }

        logger.debug("[{}]过期缓存清除，rows:{},removes:{}", name, rows.size(), removes);
    }


    public static class Row<V> {
        //正常状态
        static final int NORMAL = 0;
        //插入状态
        static final int INSERT = 1;
        //更新状态
        static final int UPDATE = 2;
        //删除状态
        static final int DELETE = 3;


        private V data;

        private int state;

        public Row(V data) {
            this.data = data;
        }

        public V getData() {
            return data;
        }

        public int getState() {
            return state;
        }
    }
}
