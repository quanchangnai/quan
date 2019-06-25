package quan.database.log;

import quan.database.Cache;
import quan.database.Data;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DataLog implements Log {

    private Key key;

    private Data data;

    private Data cacheData;

    public DataLog(Data data, Cache cache, Object key) {
        this.key = new Key(cache, key);
        this.data = data;
        this.cacheData = cache.getRecord(key);
        if (data != null && (data.getCache() != key || !data.getKey().equals(key))) {
            throw new IllegalArgumentException();
        }
    }

    public Key getKey() {
        return key;
    }


    public Data getData() {
        return data;
    }


    public DataLog setData(Data data) {
        this.data = data;
        if (data != null && (data.getCache() != key.cache || !data.getKey().equals(key.key))) {
            throw new IllegalArgumentException();
        }
        return this;
    }


    public boolean isConflict() {
        //冲突情况

        //1.缓存里的数据变了
        if (cacheData != key.cache.getRecord(key.key)) {
            return true;
        }

        //2.缓存里的数据没变，日志里的数据变了
        if (cacheData != data) {
            return true;
        }

        return false;

    }

    @Override
    public void commit() {

        if (data == null) {
            if (cacheData != null) {
                //delete
            }
        } else {
            //put
        }
        key.cache.putRecord(key.key, data);
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
