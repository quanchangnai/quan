package quan.database;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/6/24.
 */
class DataLog {

    private Key key;

    /**
     * 事务里的当前数据
     */
    private Data current;

    /**
     * 缓存里的原始数据行
     */
    private Cache.Row originRow;

    /**
     * 缓存里的原始数据
     */
    private Data originData;

    /**
     * 原始数据的状态
     */
    private int originState;

    /**
     * 记录之前没有查询的空删除
     */
    private boolean deleted;

    public DataLog(Data current, Cache.Row originRow, Data originData, int originState, Cache cache, Object key) {
        this.key = new Key(cache, key);
        this.current = current;
        this.originRow = originRow;
        this.originData = originData;
        this.originState = originState;
    }

    public DataLog setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Key getKey() {
        return key;
    }


    public Data getCurrent() {
        if (current != null) {
            current.touch();
        }
        return current;
    }

    public DataLog setCurrent(Data current) {
        this.current = current;
        if (current != null) {
            current.touch();
        }
        return this;
    }

    public Cache getCache() {
        return key.cache;
    }

    public boolean isConflict() {
        key.cache.checkClosed();

        //有可能出现事务执行时间比缓存的过期时间还长的极端情况
        long costTime = System.currentTimeMillis() - Transaction.get(true).getTaskStartTime();
        if (costTime > getCache().getCacheExpire() * 1000) {
            return true;
        }

        Cache.Row row = key.cache.getRow(key.k);
        if (originRow != row) {
            return true;
        }

        if (row != null) {
            //这里同时读row的data和state不需要加锁，是因为已经加上了行级锁
            if (originData != row.getData()) {
                return true;
            }
            if (originState != row.getState()) {
                return true;
            }
        }

        return false;

    }

    public void commit() {
        if (current == null && originData != null && originState != Cache.Row.DELETE || deleted) {
            //delete
            key.cache.setDelete(key.k);
        }
        if (current != null && (originRow == null || originState == Cache.Row.DELETE)) {
            //insert
            key.cache.setInsert(current);
        }
    }

    public static class Key {

        private Cache cache;

        private Object k;

        public Key(Cache cache, Object k) {
            this.cache = cache;
            this.k = k;
        }

        public Object getK() {
            return k;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return Objects.equals(cache, key1.cache) &&
                    Objects.equals(k, key1.k);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cache, k);
        }

    }

}
