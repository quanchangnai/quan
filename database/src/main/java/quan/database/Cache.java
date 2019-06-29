package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.util.CallerUtil;
import quan.database.exception.DbException;
import quan.database.log.DataLog;
import quan.database.log.VersionLog;

import java.util.*;
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

    private Map<K, Row<V>> dirty;

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
        dirty = new ConcurrentHashMap<>();
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
//        logger.debug("[{}],Cache.setInsert({}),rows:{}", name, data.getKey(), rows);

        Row<V> row = rows.get(data.getKey());
        //row有可能为空(新插入时)
        //可能状态:delete:(被删除过)
        //不可能状态:insert,update,normal,插入时会校验数据是否已存在
        if (row != null) {
            if (row.state == Row.INSERT || row.state == Row.UPDATE || row.state == Row.NORMAL) {
                throw new IllegalStateException("row(" + data.getKey() + "+不可能出现此状态:" + row.state + "，请检查代码逻辑");
            } else {
                row.state = Row.INSERT;
                row.data = data;//之前有可能没有查询直接删除了
            }

        } else {
            //新插入
            row = new Row<>(data, Row.INSERT);
            rows.put(data.getKey(), row);
            dirty.put(data.getKey(), row);
        }
    }

    public void setUpdate(V data) {
        CallerUtil.validCallerClass(VersionLog.class);
//        logger.debug("[{}],Cache.setUpdate({}),rows:{}", name, data.getKey(), rows);

        Row<V> row = rows.get(data.getKey());
        //row:可能为空
        //情况1:新插入的数据接着又被删除了
        //情况2:新创建的对象根本就没有插入
        //情况3:数据不是在当前事务中查出来的，有可能缓存已经过期被清理了
        if (row == null) {
            return;
        }
        //可能状态:insert,update,normal,delete(当前事务中被删除)
        if (row.state == Row.NORMAL) {
            row.state = Row.UPDATE;
            dirty.put(data.getKey(), row);
        }
    }

    public void setDelete(K key) {
        CallerUtil.validCallerClass(DataLog.class);
//        logger.debug("[{}],Cache.setDelete({}),rows:{}", name, key, rows);

        Row<V> row = rows.get(key);
        //row有可能为空(没有查询直接执行删除，这个时候还不知道有没有数据可以被删)
        //可能状态:insert(新插入的),normal(刚查询出来的),update:(查询出来后被更新过),delete(已经被删除了)
        if (row != null) {
            if (row.state == Row.INSERT) {
                //新插入的数据清除插入记录
                rows.remove(key);
                dirty.remove(key);
            } else {
                row.state = Row.DELETE;
                dirty.put(key, row);
            }
        } else {
            //设置删除记录即可
            row = new Row<>(null, Row.DELETE);
            rows.put(key, row);
            dirty.put(key, row);
        }
    }


    public V get(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getCurrent();
        }

        Row<V> originRow;

        try {
            //存档时阻塞
            lock.readLock().lock();

            originRow = rows.get(key);
            if (originRow == null) {
                V data = database.get(this, key);
                if (data != null) {
                    originRow = new Row<>(data);
                    Row<V> oldRow = rows.putIfAbsent(key, originRow);
                    if (oldRow != null) {
                        originRow = oldRow;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        Data<K> currentData = null;
        Data<K> originData = null;
        int originState = 0;

        if (originRow != null) {
            ReadWriteLock rowLock = LockPool.getLock(this, key);
            rowLock.readLock().lock();
            try {
                //加行级锁
                originData = originRow.data;
                originState = originRow.state;
                if (originRow.state != Row.DELETE) {
                    currentData = originRow.data;
                }
            } finally {
                rowLock.readLock().unlock();
            }
        }

        log = new DataLog(currentData, originRow, originData, originState, this, key);
        transaction.addDataLog(log);

        return (V) log.getCurrent();

    }

    public void delete(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get();

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            log.setCurrent(null);
            return;
        }

        Row<V> originRow = rows.get(key);

        Data<K> originData = null;
        int originState = 0;
        if (originRow != null) {
            ReadWriteLock rowLock = LockPool.getLock(this, key);
            rowLock.readLock().lock();
            try {
                //加行级锁
                originData = originRow.data;
                originState = originRow.state;
            } finally {
                rowLock.readLock().unlock();
            }
        }

        //不确定数据是否存在，增加一条删除日志，这样如果存在数据就一点会被删掉
        log = new DataLog(null, originRow, originData, originState, this, key);
        transaction.addDataLog(log);

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
            Map<K, V> inserts = new HashMap();
            Map<K, V> updates = new HashMap();
            Set<K> deletes = new HashSet<>();

            for (K key : dirty.keySet()) {
                Row<V> row = rows.get(key);
                if (row.state == Row.INSERT) {
                    row.state = Row.NORMAL;
                    inserts.put(key, row.data);
                }
                if (row.state == Row.UPDATE) {
                    row.state = Row.NORMAL;
                    updates.put(key, row.data);
                }
                if (row.state == Row.DELETE) {
                    deletes.add(key);
                }
            }

            for (V data : inserts.values()) {
                database.put(data);
            }
            for (V data : updates.values()) {
                database.put(data);
            }
            for (K key : deletes) {
                rows.remove(key);
                database.delete(this, key);
            }

            dirty.clear();

            logger.debug("[{}]存档,rows:{},inserts:{},updates:{},deletes:{}", name, rows.size(), inserts.keySet(), updates.keySet(), deletes);

            if (rows.size() > cacheSize) {
                checkExpireAndClean();
            }

        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * 检测过期缓存并清理
     */
    private void checkExpireAndClean() {
        long now = System.currentTimeMillis();
        Set<K> cleans = new HashSet<>();

        Iterator<Row<V>> iterator = rows.values().iterator();
        while (iterator.hasNext()) {
            V data = iterator.next().data;
            if (now - data.getTouchTime() > cacheExpire * 1000) {
                iterator.remove();
                cleans.add(data.getKey());
            }
        }

        logger.debug("[{}]过期缓存清理, rows.size:{},clears.size:{},rows:{},clears:{}", name, rows.size(), cleans.size(), rows, cleans);
    }


    public static class Row<V> {
        //正常状态
        public static final int NORMAL = 0;
        //插入状态
        public static final int INSERT = 1;
        //更新状态
        public static final int UPDATE = 2;
        //删除状态
        public static final int DELETE = 3;


        private V data;

        private int state;

        public Row(V data) {
            this.data = data;
        }

        public Row(V data, int state) {
            this.data = data;
            this.state = state;
        }

        public V getData() {
            return data;
        }

        public int getState() {
            return state;
        }

        @Override
        public String toString() {
            return "Row{" +
                    "data=" + data +
                    ", state=" + state +
                    '}';
        }
    }
}
