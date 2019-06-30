package quan.database.log;

import quan.database.Cache;
import quan.database.Data;
import quan.database.Transaction;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DataLog implements Log {

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

    public DataLog(Data current, Cache.Row originRow, Data originData, int originState, Cache cache, Object key) {
        this.key = new Key(cache, key);
        this.current = current;
        this.originRow = originRow;
        this.originData = originData;
        this.originState = originState;
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
        //有可能出现事务执行时间比缓存的过期时间还长的极端情况
        long costTime = System.currentTimeMillis() - Transaction.get().getTaskStartTime();
        if (costTime > getCache().getCacheExpire() * 1000) {
            return true;
        }

        Cache.Row row = key.cache.getRow(key.k);
        if (originRow != row) {
            return true;
        }

        if (row != null) {
            if (originData != row.getData()) {
                return true;
            }
            if (originState != row.getState()) {
                return true;
            }
        }

        return false;

    }

    @Override
    public void commit() {
        if (current == null && originData != null && originState != Cache.Row.DELETE) {
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
