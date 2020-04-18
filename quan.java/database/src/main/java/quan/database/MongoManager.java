package quan.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;

import java.util.*;

/**
 * MongoDB管理器
 * Created by quanchangnai on 2020/4/13.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MongoManager implements DataUpdater {

    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

    private static final ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    private String databaseName;

    private String dataPackage;

    private MongoClient client;

    private MongoDatabase database;

    private Map<Class, MongoCollection> collections = new HashMap<>();

    private MongoManager(MongoClient client, String databaseName, String dataPackage) {
        this.client = client;
        this.databaseName = databaseName;
        this.dataPackage = dataPackage;
    }

    public MongoManager(String connectionString, String databaseName, String dataPackage) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        Assertions.notNull("connectionString", connectionString);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));

        initClient(builder);
    }

    public MongoManager(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);

        initClient(builder);
    }

    private void initClient(MongoClientSettings.Builder builder) {
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry(dataPackage);
        builder.codecRegistry(CodecRegistries.fromRegistries(dataCodecRegistry, MongoClientSettings.getDefaultCodecRegistry()));
        client = MongoClients.create(builder.build());

        initDatabase();
    }

    private void initDatabase() {
        database = client.getDatabase(databaseName);
        ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection);
        Transaction.addUpdater(this);
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
            IndexOptions indexOptions = new IndexOptions().name(index.getName()).unique(index.isUnique());
            createIndexModels.add(new IndexModel(Indexes.ascending(index.getFields()), indexOptions));
        }
        if (!createIndexModels.isEmpty()) {
            collection.createIndexes(createIndexModels);
        }

    }

    public MongoManager create(String databaseName) {
        if (databaseName.equals(this.databaseName)) {
            return this;
        }
        MongoManager manager = new MongoManager(client, databaseName, dataPackage);
        manager.initDatabase();
        return manager;
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

    @Override
    public void update(List<Data> updates) {
        for (Data data : updates) {
            DataUpdater updater = data._getUpdater();
            if (updater != null && updater != this) {
                continue;
            }
            MongoCollection collection = collections.get(data.getClass());
            if (collection == null) {
                continue;
            }
            collection.replaceOne(Filters.eq(data._id()), data, replaceOptions);
        }
    }
}
