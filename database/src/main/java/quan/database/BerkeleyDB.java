package quan.database;

import com.alibaba.fastjson.JSON;
import com.sleepycat.je.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class BerkeleyDB extends Database {

    private Environment environment;

    private File dir;

    private Map<String, com.sleepycat.je.Database> dbs = new HashMap<>();

    public BerkeleyDB(File dir) {
        super();
        this.dir = dir;
        open();
    }

    public BerkeleyDB(String dir) {
        this(new File(dir));
    }

    public BerkeleyDB(String dir, int cacheSize, int cacheExpire, int storePeriod, int storeThreadNum) {
        this(new File(dir), cacheSize, cacheExpire, storePeriod, storeThreadNum);
    }

    public BerkeleyDB(File dir, int cacheSize, int cacheExpire, int storePeriod, int storeThreadNum) {
        super(cacheSize, cacheExpire, storePeriod, storeThreadNum);
        this.dir = dir;
        open();
    }

    private void open() {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setDurability(Durability.COMMIT_WRITE_NO_SYNC);

        environment = new Environment(dir, envConfig);

    }

    @Override
    protected void registerCache0(Cache cache) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        com.sleepycat.je.Database db = environment.openDatabase(null, cache.getName(), databaseConfig);
        dbs.put(db.getDatabaseName(), db);

    }

    @Override
    protected void close0() {
        environment.flushLog(true);
        dbs.values().forEach(com.sleepycat.je.Database::close);

        environment.close();
        environment = null;
        dbs.clear();
    }

    @Override
    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        checkClosed();

        DatabaseEntry keyEntry = new DatabaseEntry(key.toString().getBytes());
        DatabaseEntry dataEntry = new DatabaseEntry();

        dbs.get(cache.getName()).get(null, keyEntry, dataEntry, LockMode.DEFAULT);

        if (dataEntry.getData() == null) {
            return null;
        }

        String jsonStr = new String(dataEntry.getData());

        V data = cache.getDataFactory().apply(key);
        data.decode(JSON.parseObject(jsonStr));

        return data;
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {
        checkClosed();

        String jsonStr = data.encode().toJSONString();

        DatabaseEntry keyEntry = new DatabaseEntry(data.getKey().toString().getBytes());
        DatabaseEntry dataEntry = new DatabaseEntry(jsonStr.getBytes());

        dbs.get(data.getCache().getName()).put(null, keyEntry, dataEntry);
    }


    @Override
    protected <K, V extends Data<K>> void delete(Cache<K, V> cache, K key) {
        checkClosed();

        DatabaseEntry keyEntry = new DatabaseEntry(key.toString().getBytes());
        dbs.get(cache.getName()).delete(null, keyEntry);

    }

}
