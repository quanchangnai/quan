package quan.database;

/**
 * Created by quanchangnai on 2019/7/1.
 */
public class MongoDB extends Database {

    @Override
    protected void close0() {

    }

    @Override
    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        return null;
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {

    }

    @Override
    protected <K, V extends Data<K>> void delete(Cache<K, V> cache, K key) {

    }

}
