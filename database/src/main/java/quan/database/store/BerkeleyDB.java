package quan.database.store;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import quan.database.Data;
import quan.database.Database;
import quan.database.Cache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class BerkeleyDB extends Database {

    private Environment environment;

    private File path;

    private Map<String, com.sleepycat.je.Database> dbs = new HashMap<>();

    public BerkeleyDB(File path) {
        this.path = path;
    }

    public BerkeleyDB(String path) {
        this.path = new File(path);
    }

    @Override
    public void open() {
        if (!path.exists()) {
            path.mkdirs();
        }
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        environment = new Environment(path, envConfig);
//        environment.openDatabase(null)
    }

    @Override
    public void close() {
        environment.close();
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

    private void test() {

    }

    public static void main(String[] args) {
        BerkeleyDB db = new BerkeleyDB(".temp/bdb");
        db.open();
        db.test();
        db.close();
    }
}
