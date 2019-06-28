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

    private Data current;

    private Data origin;

    public DataLog(Data current, Data origin, Cache cache, Object key) {
        this.key = new Key(cache, key);
        this.current = current;
        this.origin = origin;
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

    public Data getOrigin() {
        return origin;
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
        //缓存里的数据变了
        if (origin != key.cache.getNoLock(key.key)) {
            return true;
        }

        //有可能出现事务执行时间比缓存的过期时间还长的极端情况
        long costTime = System.currentTimeMillis() - Transaction.get().getStartTime();
        if (costTime > getCache().getCacheExpire() * 1000) {
            return true;
        }

        return false;

    }

    @Override
    public void commit() {
        if (current == null && origin != null) {
            //delete
            key.cache.setDelete(key.key);
        }
        if (current != null && origin == null) {
            //insert
            key.cache.setInsert(current);
        }
    }

    public static class Key {

        private Cache cache;

        private Object key;

        public Key(Cache cache, Object key) {
            this.cache = cache;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return Objects.equals(cache, key1.cache) &&
                    Objects.equals(key, key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cache, key);
        }

    }

}
