package quan.database.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;
import quan.database.Data;
import quan.database.DataCodecRegistry;

import java.util.*;

/**
 * MongoDB管理器
 * Created by quanchangnai on 2020/4/13.
 */
@SuppressWarnings("unchecked")
public class MongoManager {

    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

    private MongoClient client;

    private MongoDatabase database;

    private Map<Class, MongoCollection> collections = new HashMap<>();

    private MongoUpdater updater;

    public MongoManager(String connectionString, String databaseName, String dataPackage) {
        Assertions.notNull("connectionString", connectionString);
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        init(builder, databaseName, dataPackage);
    }

    public MongoManager(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        init(builder, databaseName, dataPackage);
    }

    private void init(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        Assertions.notNull("databaseName", databaseName);
        Assertions.notNull("dataPackage", dataPackage);
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry(dataPackage);
        builder.codecRegistry(CodecRegistries.fromRegistries(dataCodecRegistry, MongoClientSettings.getDefaultCodecRegistry()));

        client = MongoClients.create(builder.build());
        database = client.getDatabase(databaseName);
        updater = new MongoUpdater(this);

        ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection);
    }

    private void initCollection(Class<?> clazz) {
        String collectionName;
        Map<String, Data.Index> collectionIndexes;

        try {
            collectionName = (String) clazz.getField("_NAME").get(clazz);
            collectionIndexes = new HashMap<>((Map<String, Data.Index>) clazz.getField("_INDEXES").get(clazz));
        } catch (Exception e) {
            logger.error("", e);
            return;
        }

        MongoCollection<?> collection = database.getCollection(collectionName, clazz);
        collections.put(clazz, collection);

        Set<String> dropIndexes = new HashSet<>();
        for (Document index : collection.listIndexes()) {
            String indexName = index.getString("name");
            if (indexName.equals("_id_")) {
                continue;
            }
            if (collectionIndexes.containsKey(indexName)) {
                collectionIndexes.remove(indexName);
            } else {
                dropIndexes.add(indexName);
            }
        }

        dropIndexes.forEach(collection::dropIndex);

        List<IndexModel> createIndexModels = new ArrayList<>();
        for (Data.Index index : collectionIndexes.values()) {
            createIndexModels.add(new IndexModel(Indexes.ascending(index.getFields()), new IndexOptions().name(index.getName()).unique(index.isUnique())));
        }
        if (!createIndexModels.isEmpty()) {
            collection.createIndexes(createIndexModels);
        }

    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public <D extends Data> MongoCollection<D> getCollection(Class<D> clazz) {
        return (MongoCollection<D>) collections.get(clazz);
    }

    public MongoUpdater getUpdater() {
        return updater;
    }
}
