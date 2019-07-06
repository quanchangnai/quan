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


    public BerkeleyDB(String dir) {
        super(new Config().setDir(dir));
    }

    public BerkeleyDB(Config config) {
        super(config);
    }

    @Override
    protected void open0() {
        dir = new File(getConfig().dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);

        environment = new Environment(dir, envConfig);

    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
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


    public static class Config extends Database.Config {
        /**
         * 数据库文件目录
         */
        private String dir;

        public String getDir() {
            return dir;
        }

        public Config setDir(String dir) {
            this.dir = dir;
            return this;
        }
    }

}