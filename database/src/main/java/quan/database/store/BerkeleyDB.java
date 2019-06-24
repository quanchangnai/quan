package quan.database.store;

import com.sleepycat.je.*;
import quan.database.Cache;
import quan.database.Data;
import quan.database.Database;

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
    }

    public BerkeleyDB(String dir) {
        super();
        this.dir = new File(dir);
    }

    public BerkeleyDB(File dir, int cacheSize, int cacheExpire) {
        super(cacheSize, cacheExpire);
        this.dir = dir;
    }

    @Override
    public void open() {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        environment = new Environment(dir, envConfig);

        for (Cache cache : getCaches().values()) {
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setAllowCreate(true);
            com.sleepycat.je.Database db = environment.openDatabase(null, cache.getName(), databaseConfig);
            dbs.put(db.getDatabaseName(), db);
        }
    }

    @Override
    public void close() {
        for (com.sleepycat.je.Database db : dbs.values()) {
            db.close();
        }
        environment.close();
    }

    @Override
    protected <K, V extends Data<K>> String doGet(String cacheName, String key) {
        DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
        DatabaseEntry dataEntry = new DatabaseEntry();

        dbs.get(cacheName).get(null, keyEntry, dataEntry, LockMode.DEFAULT);

        if (dataEntry.getData() == null) {
            return null;
        }
        return new String(dataEntry.getData());
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {
        String json = data.encode().toJSONString();

        DatabaseEntry keyEntry = new DatabaseEntry(data.getKey().toString().getBytes());
        DatabaseEntry dataEntry = new DatabaseEntry(json.getBytes());

        dbs.get(data.cache().getName()).put(null, keyEntry, dataEntry);
    }


    @Override
    protected <K, V extends Data<K>> void remove(String cacheName, K key) {
        DatabaseEntry keyEntry = new DatabaseEntry(key.toString().getBytes());
        dbs.get(cacheName).delete(null, keyEntry);
    }

}
