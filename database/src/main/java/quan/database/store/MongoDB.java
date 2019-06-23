package quan.database.store;

import quan.database.Data;
import quan.database.Database;
import quan.database.Cache;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class MongoDB extends Database {


    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        return null;
    }

    @Override
    protected <K, V extends Data<K>> V remove(Cache<K, V> cache, K key) {
        return null;
    }

    @Override
    protected <K, V extends Data<K>> V put(Cache<K, V> cache, V data) {
        return null;
    }

}
