package quan.database.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;
import quan.database.Data;
import quan.database.DataCodecRegistry;
import quan.database.DataUpdater;
import quan.database.Transaction;

import java.util.*;

/**
 * MongoDB管理器
 * Created by quanchangnai on 2020/4/13.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Mongo implements DataUpdater {

    private static final Logger logger = LoggerFactory.getLogger(Mongo.class);

    //<MongoClient, Map<databaseName, Mongo>>
    private static Map<MongoClient, Map<String, Mongo>> mongoMap = new HashMap<>();

    private static final ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    static {
        enhance();
    }

    private String databaseName;

    private String dataPackage;

    private MongoClient client;

    private MongoDatabase database;

    private Map<Class, MongoCollection> collections = new HashMap<>();

    private Mongo(MongoClient client, String databaseName, String dataPackage) {
        this.client = client;
        this.databaseName = databaseName;
        this.dataPackage = dataPackage;
    }

    public Mongo(String connectionString, String databaseName, String dataPackage) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        Assertions.notNull("connectionString", connectionString);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));

        initClient(builder);
    }

    public Mongo(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        this.databaseName = Assertions.notNull("databaseName", databaseName);
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);

        initClient(builder);
    }

    private void initClient(MongoClientSettings.Builder builder) {
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry(dataPackage);
        builder.codecRegistry(CodecRegistries.fromRegistries(dataCodecRegistry, MongoClientSettings.getDefaultCodecRegistry()));
        client = MongoClients.create(builder.build());
        mongoMap.put(client, new HashMap<>());

        initDatabase();
    }

    private void initDatabase() {
        database = client.getDatabase(databaseName);
        mongoMap.get(client).put(databaseName, this);

        ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection);

        Transaction.addUpdater(this);
    }

    private void initCollection(Class<?> clazz) {
        String collectionName;
        Map<String, Data.Index> collectionIndexes = new HashMap<>();

        try {
            collectionName = (String) clazz.getField("_NAME").get(clazz);
            List<Data.Index> indexes = (List<Data.Index>) clazz.getField("_INDEXES").get(clazz);
            indexes.forEach(index -> collectionIndexes.put(index.getName(), index));
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

    public Mongo create(String databaseName) {
        if (databaseName.equals(this.databaseName)) {
            return this;
        }
        Mongo manager = new Mongo(client, databaseName, dataPackage);
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
    public void doUpdate(List<Data<?>> updates) {
        Map<MongoCollection<Data<?>>, List<WriteModel<Data<?>>>> writeModels = new HashMap<>();

        for (Data<?> data : updates) {
            if (data._getUpdater() != this) {
                continue;
            }
            MongoCollection<Data<?>> collection = collections.get(data.getClass());
            ReplaceOneModel<Data<?>> replaceOneModel = new ReplaceOneModel<>(Filters.eq(data._id()), data, replaceOptions);
            writeModels.computeIfAbsent(collection, c -> new ArrayList<>()).add(replaceOneModel);
        }

        writeModels.forEach(MongoCollection::bulkWrite);
    }

    public static Mongo get(MongoClient mongoClient, String databaseName) {
        Map<String, Mongo> map = mongoMap.get(mongoClient);
        if (map != null) {
            return map.get(databaseName);
        }
        return null;
    }

    /**
     * 开启字节码增强功能<br/>
     * 1.设置查询出来的数据的默认更新器
     */
    public static void enhance() {
        ElementMatcher<TypeDescription> type = ElementMatchers.named("com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor");
        ElementMatcher<MethodDescription> method = ElementMatchers.named("execute").and(ElementMatchers.takesArgument(3, ClientSession.class));

        AgentBuilder.Transformer transformer = (b, t, l, m) -> b.method(method).intercept(MethodDelegation.to(Delegation.class));

        new AgentBuilder.Default()
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .type(type)
                .transform(transformer)
                .installOn(ByteBuddyAgent.install());
    }

}
