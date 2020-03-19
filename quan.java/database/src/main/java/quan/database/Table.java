package quan.database;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * 缓存表
 * Created by quanchangnai on 2019/6/21.
 */
@SuppressWarnings({"unchecked"})
public class Table<K, V extends Data<K>> implements Comparable<Table<K, V>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final AtomicInteger nextId = new AtomicInteger();

    /**
     * 自增长的ID
     */
    private int id = nextId.incrementAndGet();

    private String name;

    private Database database;

    private Function<K, V> dataFactory;

    /**
     * 缓存过期时间(秒)
     */
    private int cacheExpire;

    /**
     * 下次清理缓存的时间
     */
    private long nextCleanTime;

    /**
     * 缓存的所有数据行
     */
    private Map<K, Row<V>> rows;

    private Map<K, Row<V>> dirty;

    /**
     * 表级锁，存档时加写锁，其他时候加读锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Table(String name, Function<K, V> dataFactory) {
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

    public Function<K, V> getDataFactory() {
        return dataFactory;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    void init(Database database) {
        lock.writeLock().lock();
        try {
            if (isWorkable()) {
                return;
            }
            this.database = database;
            cacheExpire = database.getConfig().getCacheExpire();
            nextCleanTime = System.currentTimeMillis() + cacheExpire * 1000;
            rows = new ConcurrentHashMap<>();
            dirty = new ConcurrentHashMap<>();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int compareTo(Table<K, V> other) {
        return this.id - other.id;
    }


    ReadWriteLock getLock() {
        return lock;
    }

    Row<V> getRow(K key) {
        return rows.get(key);
    }

    void setInsert(V data) {
//        logger.debug("[{}],Table.setInsert({}),rows:{}", name, data.getKey(), rows);
        data._setExpired(false);
        Row<V> row = rows.get(data.getKey());
        //row有可能为空(新插入时)
        //可能状态:delete:(被删除过)
        //不可能状态:insert,update,normal,插入时会校验数据是否已存在
        if (row != null) {
            if (row.state == Row.INSERT || row.state == Row.UPDATE || row.state == Row.NORMAL) {
                throw new IllegalStateException("row[" + data.getKey() + "]不可能出现此状态:" + row.state + "，请检查代码逻辑");
            } else {
                //之前有可能没有查询直接删除了
                row.setDataInsert(data);
            }

        } else {
            //新插入
            row = new Row<>(data, Row.INSERT);
            rows.put(data.getKey(), row);
            dirty.put(data.getKey(), row);
        }
    }

    void setUpdate(V data) {
//        logger.debug("[{}],Table.setUpdate({}),rows:{}", name, data.getKey(), rows);

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

    void setDelete(K key) {
//        logger.debug("[{}],Table.setDelete({}),rows:{}", name, key, rows);

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

        Transaction transaction = Transaction.get(true);

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            return (V) log.getCurrent();
        }

        Row<V> row;

        try {
            //存档时阻塞
            lock.readLock().lock();

            row = rows.get(key);
            if (row == null) {
                V data = database.get(this, key);
                if (data != null) {
                    row = new Row<>(data);
                    Row<V> oldRow = rows.putIfAbsent(key, row);
                    if (oldRow != null) {
                        row = oldRow;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        Pair<V, Integer> dataState = row == null ? null : row.getDataState();

        V current = null;
        V data = null;
        int state = Row.NORMAL;

        if (dataState != null) {
            data = dataState.getLeft();
            state = dataState.getRight();
            if (state != Row.DELETE) {
                current = data;
            }
        }

        log = new DataLog(current, row, data, state, this, key);
        transaction.addDataLog(log);

        return current;

    }

    public void delete(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Transaction transaction = Transaction.get(true);

        DataLog log = transaction.getDataLog(new DataLog.Key(this, key));
        if (log != null) {
            log.setDeleted();
            return;
        }

        Row<V> row = rows.get(key);
        Pair<V, Integer> dataState = row == null ? null : row.getDataState();

        V data = null;
        int state = Row.NORMAL;

        if (dataState != null) {
            data = dataState.getLeft();
            state = dataState.getRight();
        }

        //数据不一定存在，增加删除日志，这样如果存在数据就一点会被删掉
        log = new DataLog(null, row, data, state, this, key);
        log.setDeleted();
        transaction.addDataLog(log);
    }

    public void insert(V data) {
        Objects.requireNonNull(data, "数据不能为空");
        Objects.requireNonNull(data._getTable(), "数据不支持持久化");
        K key = Objects.requireNonNull(data.getKey(), "数据主键不能为空");

        if (get(key) != null) {
            throw new DbException("数据已存在");
        }

        DataLog log = Transaction.get(true).getDataLog(new DataLog.Key(this, key));
        log.setCurrent(data);
    }

    public V getOrInsert(K key) {
        V data = get(key);
        if (data != null) {
            return data;
        }

        data = dataFactory.apply(key);

        DataLog log = Transaction.get(true).getDataLog(new DataLog.Key(this, data.getKey()));
        log.setCurrent(data);

        return data;
    }

    /**
     * 存档数据，并尝试清理过期缓存
     */
    public void save() {
        lock.writeLock().lock();
        try {
            save0();
            cleanExpired();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 手动存档数据，如果数据不需要存档则直接返回
     */
    public void save(K key) {
        Objects.requireNonNull(key, "主键不能为空");

        Row<V> row = dirty.remove(key);
        if (row == null) {
            return;
        }

        Pair<V, Integer> dataState = row.getDataState();
        V data = dataState.getLeft();
        if (dataState.getRight() == Row.DELETE) {
            database.delete(this, key);
        } else {
            database.put(data);
        }
    }

    private void save0() {
        long startTime = System.currentTimeMillis();

        int insertNum = 0;
        int updateNum = 0;
        Set<V> puts = new HashSet<>();
        Set<K> deletes = new HashSet<>();

        for (K key : dirty.keySet()) {
            Row<V> row = dirty.get(key);
            if (row.state == Row.INSERT) {
                row.state = Row.NORMAL;
                puts.add(row.data);
                insertNum++;
            }
            if (row.state == Row.UPDATE) {
                row.state = Row.NORMAL;
                puts.add(row.data);
                updateNum++;
            }
            if (row.state == Row.DELETE) {
                deletes.add(key);
            }
        }

        try {
            database.bulkWrite(this, puts, deletes);

            for (K key : dirty.keySet()) {
                Row<V> row = dirty.get(key);
                if (row.state == Row.DELETE && row.data != null) {
                    row.data._setExpired(true);
                }
            }

            dirty.clear();
        } catch (Exception e) {
            logger.error("存档出错", e);
        }

        long costTime = System.currentTimeMillis() - startTime;

        logger.debug("[{}]存档耗时:{}ms,当前缓存数量:{},插入数量:{},更新数量:{},删除数量:{}", name, costTime, rows.size(), insertNum, updateNum, deletes.size());
    }

    void finalSave() {
        lock.writeLock().lock();
        try {
            save0();
            for (Row<V> row : rows.values()) {
                if (row.data != null) {
                    row.data._setExpired(true);
                }
            }
            rows.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isWorkable() {
        return database != null && !database.isClosed();
    }

    public void checkWorkable() {
        if (database == null) {
            throw new DbException("缓存未注册到数据库");
        }
        if (database.isClosed()) {
            throw new DbException("数据库已关闭");
        }
    }

    /**
     * 清理过期缓存
     */
    private void cleanExpired() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < nextCleanTime) {
            return;
        }

        Set<K> cleans = new HashSet<>();

        Iterator<Row<V>> iterator = rows.values().iterator();
        while (iterator.hasNext()) {
            V data = iterator.next().data;
            if (currentTime - data.touchTime() > cacheExpire * 1000) {
                data._setExpired(true);
                iterator.remove();
                cleans.add(data.getKey());
            }
        }

        nextCleanTime = currentTime + cacheExpire * 500;

        logger.debug("[{}]清理过期缓存, rows.size:{},cleans.size:{},rows:{},cleans:{}", name, rows.size(), cleans.size(), rows, cleans);
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

        private ReadWriteLock lock = new ReentrantReadWriteLock();

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

        void setDataInsert(V data) {
            lock.writeLock().lock();
            try {
                this.data = data;
                this.state = INSERT;
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Pair<V, Integer> getDataState() {
            lock.readLock().lock();
            try {
                return Pair.of(data, state);
            } finally {
                lock.readLock().unlock();
            }

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