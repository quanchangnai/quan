package quan.database;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/1.
 */
public class MongoDB extends Database {

    private static final String _ID = "_id";

    private static final String $SET = "$set";

    private static final UpdateOptions updateOptions = new UpdateOptions().upsert(true);

    private MongoClient client;

    private MongoDatabase database;

    private Map<String, MongoCollection> collections = new HashMap<>();

    public MongoDB(Config config) {
        super(config);
    }


    @Override
    protected void open0() {
        MongoClientURI clientURI = new MongoClientURI(getConfig().connectionString);
        client = new MongoClient(clientURI);
        database = client.getDatabase(clientURI.getDatabase());
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
        collections.get(data.getCache().getName()).updateOne(filter, updates, updateOptions);
    }

    @Override
    protected <K, V extends Data<K>> void delete(Cache<K, V> cache, K key) {
        checkClosed();
        Document filter = new Document(_ID, key);
        collections.get(cache.getName()).deleteOne(filter);
    }

    @Override
    protected <K, V extends Data<K>> void bulkWrite(Cache<K, V> cache, Set<V> puts, Set<K> deletes) {
        checkClosed();

        List<WriteModel> writeModels = new ArrayList<>();

        for (V putData : puts) {
            Document filter = new Document(_ID, putData.getKey());
            Document updates = new Document($SET, putData.encode());
            UpdateOneModel updateOneModel = new UpdateOneModel(filter, updates, updateOptions);
            writeModels.add(updateOneModel);
        }

        for (K deleteKey : deletes) {
            Document filter = new Document(_ID, deleteKey);
            DeleteOneModel deleteOneModel = new DeleteOneModel(filter);
            writeModels.add(deleteOneModel);
        }

        if (!writeModels.isEmpty()) {
            collections.get(cache.getName()).bulkWrite(writeModels);
        }
    }

    public static class Config extends Database.Config {

        /**
         * 连接字符串，例如 mongodb://username:password@localhost:27017/test?maxPoolSize=5
         */
        private String connectionString;


        public String getConnectionString() {
            return connectionString;
        }

        public Config setConnectionString(String connectionString) {
            this.connectionString = connectionString;
            return this;
        }
    }

}
