package quan.mongo;

import com.mongodb.*;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoClient;
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
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;
import quan.data.Data;
import quan.data.DataCodecRegistry;
import quan.data.DataWriter;

import java.util.*;

/**
 * 数据库
 * Created by quanchangnai on 2020/4/13.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Database implements DataWriter, MongoDatabase {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    static Map<MongoClient, Map<String/*databaseName*/, Database>> databases = new HashMap<>();

    private static final ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    /**
     * 数据类所在的包名
     */
    private String dataPackage;

    private MongoClient client;

    private MongoDatabase database;

    private Map<Class, MongoCollection> collections = new HashMap<>();

    private Database() {
    }

    /**
     * 简单的数据库对象构造方法
     *
     * @param connectionString MongoDB连接字符串
     * @param databaseName     数据库名
     * @param dataPackage      数据类所在的包名
     */
    public Database(String connectionString, String databaseName, String dataPackage) {
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        Assertions.notNull("connectionString", connectionString);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));

        initClient(builder, databaseName);
    }

    public Database(MongoClientSettings.Builder builder, String databaseName, String dataPackage) {
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        initClient(builder, databaseName);
    }

    private void initClient(MongoClientSettings.Builder builder, String databaseName) {
        Assertions.notNull("databaseName", databaseName);
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry(dataPackage);
        builder.codecRegistry(CodecRegistries.fromRegistries(dataCodecRegistry, MongoClientSettings.getDefaultCodecRegistry()));
        client = MongoClients.create(builder.build());
        databases.put(client, new HashMap<>());

        initDatabase(databaseName);
    }

    private void initDatabase(String databaseName) {
        database = client.getDatabase(databaseName);
        databases.get(client).put(databaseName, this);
        ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection);
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

    /**
     * 使用当前数据库实例在同一个MongoClient上获取一个新的数据库实例，它使用的数据类和当前数据库实例使用的完全一样
     *
     * @param databaseName 新的数据库名
     * @return 如果给定的数据库名和当前数据库名相同，将直接返回当前数据库实例
     */
    public Database getInstance(String databaseName) {
        if (databaseName.equals(database.getName())) {
            return this;
        }
        Database database = new Database();
        database.client = client;
        database.dataPackage = dataPackage;
        database.initDatabase(databaseName);
        return database;
    }

    /**
     * 使用当前数据库实例在同一个MongoClient上获取一个新的数据库实例
     *
     * @param databaseName 新的数据库名
     * @param dataPackage  数据类包名
     * @return 如果给定的数据库名、数据类包名和当前数据库名、数据类包名都一样，将直接返回当前数据库实例
     */
    public Database getInstance(String databaseName, String dataPackage) {
        if (databaseName.equals(database.getName())) {
            if (this.dataPackage.equals(dataPackage)) {
                return this;
            } else {
                throw new IllegalArgumentException(String.format("新数据库名[%s]和当前数据库名[%s]一样，但是新数据包名[%s]和当前数据包名[%s]却不一样", databaseName, this.database, dataPackage, this.dataPackage));
            }
        }
        Database database = new Database();
        database.client = client;
        database.dataPackage = dataPackage;
        database.initDatabase(databaseName);
        return database;
    }

    public MongoClient getClient() {
        return client;
    }

    /**
     * 通过指定的具体数据类获取集合
     */
    public <D extends Data> MongoCollection<D> getCollection(Class<D> clazz) {
        return (MongoCollection<D>) collections.get(clazz);
    }

    @Override
    public void write(List<Data<?>> insertions, List<Data<?>> updates, List<Data<?>> deletions) {
        Map<MongoCollection<Data<?>>, List<WriteModel<Data<?>>>> writeModels = new HashMap<>();

        for (Data<?> data : insertions) {
            writeModels.computeIfAbsent(collections.get(data.getClass()), c -> new ArrayList<>()).add(new InsertOneModel(data));
        }

        for (Data<?> data : updates) {
            ReplaceOneModel<Data<?>> replaceOneModel = new ReplaceOneModel<>(Filters.eq(data._id()), data, replaceOptions);
            writeModels.computeIfAbsent(collections.get(data.getClass()), c -> new ArrayList<>()).add(replaceOneModel);
        }

        for (Data<?> data : deletions) {
            writeModels.computeIfAbsent(collections.get(data.getClass()), c -> new ArrayList<>()).add(new DeleteOneModel<>(Filters.eq(data._id())));
        }

        writeModels.forEach(MongoCollection::bulkWrite);
    }


    static {
        enhance();
    }

    /**
     * 开启字节码增强功能<br/>
     * 1.设置从数据库中查询出来的数据的默认更新器
     * 2.禁止在内存事务中写数据库
     */
    private static void enhance() {
        ElementMatcher<TypeDescription> type = ElementMatchers.named("com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor");
        ElementMatcher<MethodDescription> readMethod = ElementMatchers.named("execute").and(ElementMatchers.takesArgument(3, ClientSession.class));
        ElementMatcher<MethodDescription> writeMethod = ElementMatchers.named("execute").and(ElementMatchers.takesArgument(2, ClientSession.class));

        AgentBuilder.Transformer readTransformer = (b, t, c, m) -> b.method(readMethod).intercept(MethodDelegation.to(ReadDelegation.class));
        AgentBuilder.Transformer writeTransformer = (b, t, c, m) -> b.method(writeMethod).intercept(MethodDelegation.to(WriteDelegation.class));

        new AgentBuilder.Default()
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .type(type)
                .transform(readTransformer)
                .transform(writeTransformer)
                .installOn(ByteBuddyAgent.install());
    }


    //下面都是代理MongoDatabase的方法

    @Override
    public String getName() {
        return database.getName();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return database.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return database.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return database.getWriteConcern();
    }

    @Override
    public ReadConcern getReadConcern() {
        return database.getReadConcern();
    }

    @Override
    public MongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
        return database.withCodecRegistry(codecRegistry);
    }

    @Override
    public MongoDatabase withReadPreference(ReadPreference readPreference) {
        return database.withReadPreference(readPreference);
    }

    @Override
    public MongoDatabase withWriteConcern(WriteConcern writeConcern) {
        return database.withWriteConcern(writeConcern);
    }

    @Override
    public MongoDatabase withReadConcern(ReadConcern readConcern) {
        return database.withReadConcern(readConcern);
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> tDocumentClass) {
        return database.getCollection(collectionName, tDocumentClass);
    }

    @Override
    public Document runCommand(Bson command) {
        return database.runCommand(command);
    }

    @Override
    public Document runCommand(Bson command, ReadPreference readPreference) {
        return database.runCommand(command, readPreference);
    }

    @Override
    public <TResult> TResult runCommand(Bson command, Class<TResult> tResultClass) {
        return database.runCommand(command, tResultClass);
    }

    @Override
    public <TResult> TResult runCommand(Bson command, ReadPreference readPreference, Class<TResult> tResultClass) {
        return database.runCommand(command, readPreference, tResultClass);
    }

    @Override
    public Document runCommand(ClientSession clientSession, Bson command) {
        return database.runCommand(clientSession, command);
    }

    @Override
    public Document runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference) {
        return database.runCommand(clientSession, command, readPreference);
    }

    @Override
    public <TResult> TResult runCommand(ClientSession clientSession, Bson command, Class<TResult> tResultClass) {
        return database.runCommand(clientSession, command, tResultClass);
    }

    @Override
    public <TResult> TResult runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference, Class<TResult> tResultClass) {
        return database.runCommand(clientSession, command, readPreference, tResultClass);
    }

    @Override
    public void drop() {
        database.drop();
    }

    @Override
    public void drop(ClientSession clientSession) {
        database.drop(clientSession);
    }

    @Override
    public MongoIterable<String> listCollectionNames() {
        return database.listCollectionNames();
    }

    @Override
    public ListCollectionsIterable<Document> listCollections() {
        return database.listCollections();
    }

    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> tResultClass) {
        return database.listCollections(tResultClass);
    }

    @Override
    public MongoIterable<String> listCollectionNames(ClientSession clientSession) {
        return database.listCollectionNames(clientSession);
    }

    @Override
    public ListCollectionsIterable<Document> listCollections(ClientSession clientSession) {
        return database.listCollections(clientSession);
    }

    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(ClientSession clientSession, Class<TResult> tResultClass) {
        return database.listCollections(clientSession, tResultClass);
    }

    @Override
    public void createCollection(String collectionName) {
        database.createCollection(collectionName);
    }

    @Override
    public void createCollection(String collectionName, CreateCollectionOptions createCollectionOptions) {
        database.createCollection(collectionName, createCollectionOptions);
    }

    @Override
    public void createCollection(ClientSession clientSession, String collectionName) {
        database.createCollection(clientSession, collectionName);
    }

    @Override
    public void createCollection(ClientSession clientSession, String collectionName, CreateCollectionOptions createCollectionOptions) {
        database.createCollection(clientSession, collectionName, createCollectionOptions);
    }

    @Override
    public void createView(String viewName, String viewOn, List<? extends Bson> pipeline) {
        database.createView(viewName, viewOn, pipeline);
    }

    @Override
    public void createView(String viewName, String viewOn, List<? extends Bson> pipeline, CreateViewOptions createViewOptions) {
        database.createView(viewName, viewOn, pipeline, createViewOptions);
    }

    @Override
    public void createView(ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline) {
        database.createView(clientSession, viewName, viewOn, pipeline);
    }

    @Override
    public void createView(ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline, CreateViewOptions createViewOptions) {
        database.createView(clientSession, viewName, viewOn, pipeline, createViewOptions);
    }

    @Override
    public ChangeStreamIterable<Document> watch() {
        return database.watch();
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
        return database.watch(tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
        return database.watch(pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return database.watch(pipeline, tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
        return database.watch(clientSession);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> tResultClass) {
        return database.watch(clientSession, tResultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> pipeline) {
        return database.watch(clientSession, pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return database.watch(clientSession, pipeline, tResultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
        return database.aggregate(pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return database.aggregate(pipeline, tResultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(ClientSession clientSession, List<? extends Bson> pipeline) {
        return database.aggregate(clientSession, pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
        return database.aggregate(clientSession, pipeline, tResultClass);
    }

}
