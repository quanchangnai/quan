package quan.database.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;
import quan.database.Data;
import quan.database.DataCodecRegistry;

import java.util.*;

/**
 * Created by quanchangnai on 2020/4/13.
 */
@SuppressWarnings("unchecked")
public class MongoManager {

    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

    private MongoClient client;

    private MongoDatabase database;

    private Map<Class, MongoCollection> collections = new HashMap<>();

    public MongoManager(String connectionString, String databaseName, String dataPackage) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(Objects.requireNonNull(connectionString)));
        init(builder, databaseName, dataPackage);
    }

    public MongoManager(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        init(builder, databaseName, dataPackage);
    }

    private void init(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry(Objects.requireNonNull(dataPackage));
        builder.codecRegistry(CodecRegistries.fromRegistries(dataCodecRegistry, MongoClientSettings.getDefaultCodecRegistry()));

        client = MongoClients.create(builder.build());
        database = client.getDatabase(Objects.requireNonNull(databaseName));

        ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection);
    }

    private void initCollection(Class<?> clazz) {
        String collectionName;
        Map<String, Data.Index> collectionIndexes;
        try {
            collectionName = (String) clazz.getField("_NAME").get(clazz);
            collectionIndexes = (Map<String, Data.Index>) clazz.getField("_INDEXES").get(clazz);
        } catch (Exception e) {
            logger.error("", e);
            return;

        }

        MongoCollection<?> collection = database.getCollection(collectionName, clazz);
        collections.put(clazz, collection);

        Set<String> dropIndexes = new HashSet<>();
        for (Document index : collection.listIndexes()) {
            String indexName = index.getString("name");
            if (((Document) index.get("key")).containsKey(Data._ID)) {
                continue;
            }
            if (collectionIndexes.containsKey(indexName)) {
                collectionIndexes.remove(indexName);
            } else {
                dropIndexes.add(indexName);
            }
        }

        List<IndexModel> createIndexModels = new ArrayList<>();
        for (Data.Index index : collectionIndexes.values()) {
            Document document = new Document();
            for (String field : index.getFields()) {
                document.put(field, 1);
            }
            createIndexModels.add(new IndexModel(document, new IndexOptions().unique(index.isUnique())));
        }
        collection.createIndexes(createIndexModels);

        for (String dropIndex : dropIndexes) {
            collection.dropIndex(dropIndex);
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

}
