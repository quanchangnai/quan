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

    private long version;

    private boolean removed;

    public DataLog(Data data, Cache cache, Object key) {
        this.key = new Key(cache, key);
        this.data = data;
        if (data != null) {
            this.version = data.getVersion();
            if (data.cache() != cache || !data.getKey().equals(key)) {
                throw new IllegalArgumentException();
            }
        }
    }

    public Key getKey() {
        return key;
    }


    public Data getData() {
        return data;
    }

    public boolean isRemoved() {
        return removed;
    }

    public DataLog setData(Data data) {
        this.data = data;
        if (data != null && (data.cache() != key.cache || !data.getKey().equals(key.key))) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public DataLog setRemoved(boolean removed) {
        this.removed = removed;
        return this;
    }

    public boolean isConflict() {
        //冲突情况

        //1.和缓存里现有的数据不一致
        Data cacheRecord = key.cache.getRecord(key.key);
        if (cacheRecord != data) {
            return true;
        }

        //2.版本号不一致
        if (data != null && version != data.getVersion()) {
            return true;
        }

        return false;

    }

    @Override
    public void commit() {
        if (data != null) {
            data.versionUp();
        }
        if (removed) {
            key.cache.putRecord(key.key, null);
        } else {
            Data cacheRecord = key.cache.getRecord(key.key);
            if (cacheRecord != data) {
                key.cache.putRecord(key.key, data);
            }
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
