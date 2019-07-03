package quan.database;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/7/1.
 */
public class MongoDB extends Database {

    private static final String _ID = "_id";

    private static final String $SET = "$set";

    private MongoClient client;

    private MongoDatabase database;

    private Map<String, MongoCollection> collections = new HashMap<>();

    public MongoDB(Config config) {
        super(config);
    }


    @Override
    protected void open() {
        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.minConnectionsPerHost(1);
        optionsBuilder.connectionsPerHost(getConfig().connectionsNum);

        MongoClientURI clientURI = new MongoClientURI(getConfig().clientUri, optionsBuilder);
        client = new MongoClient(clientURI);
        database = client.getDatabase(getConfig().databaseName);
    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    @Override
    protected void close0() {
        client.close();
        collections.clear();
        client = null;
        database = null;
    }

    @Override
    protected void registerCache0(Cache cache) {
        collections.put(cache.getName(), database.getCollection(cache.getName()));
    }

    @Override
    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        checkClosed();
        Document filter = new Document(_ID, key);
        Document document = (Document) collections.get(cache.getName()).find(filter).first();
        if (document == null) {
            return null;
        }
        V data = cache.getDataFactory().apply(key);
        data.decode(new JSONObject(document));
        return data;
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {
        checkClosed();
        K key = data.getKey();
        Document filter = new Document(_ID, key);
        Document updates = new Document($SET, data.encode());
        collections.get(data.getCache().getName()).updateOne(filter, updates, new UpdateOptions().upsert(true));
    }

    @Override
    protected <K, V extends Data<K>> void delete(Cache<K, V> cache, K key) {
        checkClosed();
        Document filter = new Document(_ID, key);
        collections.get(cache.getName()).deleteOne(filter);
    }


    public static class Config extends Database.Config {

        /**
         * Mongo客户端连接URI
         */
        private String clientUri;

        /**
         * 数据库名
         */
        private String databaseName;

        /**
         * 连接数
         */
        private int connectionsNum = 5;

        public String getClientUri() {
            return clientUri;
        }

        public Config setClientUri(String clientUri) {
            this.clientUri = clientUri;
            return this;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public Config setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public int getConnectionsNum() {
            return connectionsNum;
        }

        public Config setConnectionsNum(int connectionsNum) {
            if (connectionsNum > 0) {
                this.connectionsNum = connectionsNum;
            }
            return this;
        }
    }
}
