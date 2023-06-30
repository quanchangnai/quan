package quan.data.mongo;

import com.mongodb.*;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Data;
import quan.data.DataWriter;
import quan.data.Index;
import quan.util.ClassUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * MongoDB数据库封装
 */
@SuppressWarnings({"unchecked", "rawtypes", "NullableProblems"})
public class Database implements DataWriter, MongoDatabase, Executor {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static final Map<MongoClient, Map<String/*databaseName*/, Database>> databases = new HashMap<>();

    private static final ReadWriteLock databasesLock = new ReentrantReadWriteLock();

    static final Map<MongoClient, List<ExecutorService>> clientsExecutors = new ConcurrentHashMap<>();

    private static final ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    /**
     * 数据类所在的包名
     */
    private final String dataPackage;

    private MongoClient client;

    private MongoDatabase db;

    private final Map<Class, MongoCollection> collections = new HashMap<>();

    private final List<ExecutorService> executors = new ArrayList<>();

    static {
        ClassUtils.initAop();
    }

    /**
     * 简单的数据库对象构造方法
     *
     * @param connectionString 连接字符串，参考{@link ConnectionString}
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

    public Database(MongoClientSettings.Builder clientSettings, String databaseName, String dataPackage) {
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        initClient(clientSettings, databaseName);
    }

    public Database(MongoClient client, String databaseName, String dataPackage) {
        this.client = Assertions.notNull("client", client);
        this.dataPackage = Assertions.notNull("dataPackage", dataPackage);
        Assertions.notNull("databaseName", databaseName);

        initExecutors();
        initDatabase(databaseName);
    }

    private void initClient(MongoClientSettings.Builder clientSettings, String databaseName) {
        Assertions.notNull("databaseName", databaseName);

        clientSettings.codecRegistry(CodecRegistries.fromProviders(DataCodecProvider.getDefault(), MongoClientSettings.getDefaultCodecRegistry()));
        client = MongoClients.create(clientSettings.build());

        initExecutors();
        initDatabase(databaseName);
    }

    private void initExecutors() {
        if (clientsExecutors.containsKey(client)) {
            return;
        }

        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .wrappedFactory(OperationThread::new)
                .namingPattern("database-thread-%d")
                .daemon(true).build();

        for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
            executors.add(Executors.newSingleThreadExecutor(threadFactory));
        }

        clientsExecutors.put(client, executors);
    }

    private void initDatabase(String databaseName) {
        db = client.getDatabase(databaseName);

        try {
            databasesLock.writeLock().lock();
            databases.computeIfAbsent(client, k -> new HashMap<>()).put(databaseName, this);
        } finally {
            databasesLock.writeLock().unlock();
        }

        try {
            getExecutor().submit(() -> ClassUtils.loadClasses(dataPackage, Data.class).forEach(this::initCollection)).get();
        } catch (Exception e) {
            throw new MongoException("初始化数据库集合失败", e);
        }

    }

    static Database getDatabase(MongoClient client, String databaseName) {
        databasesLock.readLock().lock();
        try {
            return databases.get(client).get(databaseName);
        } finally {
            databasesLock.readLock().unlock();
        }
    }

    private void initCollection(Class<?> clazz) {
        String collectionName = Data.name((Class<? extends Data<?>>) clazz);
        if (collectionName == null) {
            logger.error("{}._NAME未定义", clazz.getName());
            return;
        }

        MongoCollection<?> collection = db.getCollection(collectionName, clazz);
        collections.put(clazz, collection);

        Map<String, Index> collectionIndexes = new HashMap<>();
        Index.List indexes = clazz.getAnnotation(Index.List.class);
        if (indexes != null) {
            for (Index index : indexes.value()) {
                collectionIndexes.put(index.name(), index);
            }
        }

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
        for (Index index : collectionIndexes.values()) {
            IndexOptions indexOptions = new IndexOptions().name(index.name());
            if (index.type() == Index.Type.TEXT) {
                List<Bson> textIndexes = new ArrayList<>();
                for (String field : index.fields()) {
                    textIndexes.add(Indexes.text(field));
                }
                createIndexModels.add(new IndexModel(Indexes.compoundIndex(textIndexes), indexOptions));
            } else {
                indexOptions.unique(index.type() == Index.Type.UNIQUE);
                createIndexModels.add(new IndexModel(Indexes.ascending(index.fields()), indexOptions));
            }

        }
        if (!createIndexModels.isEmpty()) {
            collection.createIndexes(createIndexModels);
        }

    }

    public MongoClient getClient() {
        return client;
    }

    /**
     * 随机选择一个线程执行指定的任务
     */
    @Override
    public void execute(Runnable task) {
        getExecutor().execute(task);
    }

    /**
     * 随机选择一个线程执行指定的任务，并且通过外部指定的执行器消费结果
     *
     * @param supplier 需要执行的带结果的任务，一般来说是数据库查询任务
     * @param consumer 接收结果的消费者
     * @param executor 接收结果的执行器
     * @param <R>      结果泛型
     */
    public <R> void execute(Supplier<R> supplier, Consumer<R> consumer, Executor executor) {
        if (supplier == null || consumer == null || executor == null) {
            throw new NullPointerException("参数不能为空");
        }
        execute(() -> {
            R r = supplier.get();
            executor.execute(() -> consumer.accept(r));
        });
    }

    /**
     * 获取指定的数据类{@link Data}对应的执行器
     */
    public <D extends Data> ExecutorService getExecutor(Class<D> clazz) {
        int index = (clazz.hashCode() & 0x7FFFFFFF) % executors.size();
        return executors.get(index);
    }

    public ExecutorService getExecutor() {
        int index = RandomUtils.nextInt(0, executors.size());
        return executors.get(index);
    }

    /**
     * 获取指定的数据类对应的集合
     */
    public <D extends Data> MongoCollection<D> getCollection(Class<D> clazz) {
        return (MongoCollection<D>) collections.get(clazz);
    }

    /**
     * 通过主键_id查询数据
     *
     * @param <D> @see {@link Data}
     */
    public <D extends Data, I> D find(Class<D> clazz, I _id) {
        return find(clazz, Filters.eq(Data._ID, _id)).first();
    }

    /**
     * 通用查询数据
     *
     * @param <D>    @see {@link Data}
     * @param filter @see {@link Filters}
     */
    public <D extends Data> FindIterable<D> find(Class<D> clazz, Bson filter) {
        MongoCollection<D> collection = getCollection(clazz);
        if (collection == null) {
            throw new IllegalArgumentException("数据类[" + clazz + "]未注册");
        }
        return collection.find(filter);
    }

    /**
     * @see DataWriter#write(Set, Set)
     */
    @Override
    public void write(Set<Data<?>> saves, Set<Data<?>> deletes) {
        Map<MongoCollection<Data<?>>, List<WriteModel<Data<?>>>> writeModels = new HashMap<>();


        if (saves != null) {
            for (Data<?> data : saves) {
                ReplaceOneModel<Data<?>> replaceOneModel = new ReplaceOneModel<>(Filters.eq(data.id()), data, replaceOptions);
                writeModels.computeIfAbsent(collections.get(data.getClass()), this::list).add(replaceOneModel);
            }
        }

        if (deletes != null) {
            for (Data<?> data : deletes) {
                writeModels.computeIfAbsent(collections.get(data.getClass()), this::list).add(new DeleteOneModel<>(Filters.eq(data.id())));
            }
        }

        if (!clientsExecutors.containsKey(client)) {
            logger.error("MongoClient已经关闭了，数据无法写入到数据库");
            return;
        }

        for (MongoCollection<Data<?>> collection : writeModels.keySet()) {
            getExecutor(collection.getDocumentClass()).execute(() -> collection.bulkWrite(writeModels.get(collection)));
        }
    }

    private <K, V> ArrayList<V> list(K k) {
        return new ArrayList<>();
    }


    //下面都是代理MongoDatabase的方法

    @Override
    public String getName() {
        return db.getName();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return db.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return db.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return db.getWriteConcern();
    }

    @Override
    public ReadConcern getReadConcern() {
        return db.getReadConcern();
    }

    @Override
    public MongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
        return db.withCodecRegistry(codecRegistry);
    }

    @Override
    public MongoDatabase withReadPreference(ReadPreference readPreference) {
        return db.withReadPreference(readPreference);
    }

    @Override
    public MongoDatabase withWriteConcern(WriteConcern writeConcern) {
        return db.withWriteConcern(writeConcern);
    }

    @Override
    public MongoDatabase withReadConcern(ReadConcern readConcern) {
        return db.withReadConcern(readConcern);
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> documentClass) {
        return db.getCollection(collectionName, documentClass);
    }

    @Override
    public Document runCommand(Bson command) {
        return db.runCommand(command);
    }

    @Override
    public Document runCommand(Bson command, ReadPreference readPreference) {
        return db.runCommand(command, readPreference);
    }

    @Override
    public <TResult> TResult runCommand(Bson command, Class<TResult> resultClass) {
        return db.runCommand(command, resultClass);
    }

    @Override
    public <TResult> TResult runCommand(Bson command, ReadPreference readPreference, Class<TResult> resultClass) {
        return db.runCommand(command, readPreference, resultClass);
    }

    @Override
    public Document runCommand(ClientSession clientSession, Bson command) {
        return db.runCommand(clientSession, command);
    }

    @Override
    public Document runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference) {
        return db.runCommand(clientSession, command, readPreference);
    }

    @Override
    public <TResult> TResult runCommand(ClientSession clientSession, Bson command, Class<TResult> resultClass) {
        return db.runCommand(clientSession, command, resultClass);
    }

    @Override
    public <TResult> TResult runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference,
                                        Class<TResult> resultClass) {
        return db.runCommand(clientSession, command, readPreference, resultClass);
    }

    @Override
    public void drop() {
        db.drop();
    }

    @Override
    public void drop(ClientSession clientSession) {
        db.drop(clientSession);
    }

    @Override
    public MongoIterable<String> listCollectionNames() {
        return db.listCollectionNames();
    }

    @Override
    public ListCollectionsIterable<Document> listCollections() {
        return db.listCollections();
    }

    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> resultClass) {
        return db.listCollections(resultClass);
    }

    @Override
    public MongoIterable<String> listCollectionNames(ClientSession clientSession) {
        return db.listCollectionNames(clientSession);
    }

    @Override
    public ListCollectionsIterable<Document> listCollections(ClientSession clientSession) {
        return db.listCollections(clientSession);
    }

    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(ClientSession clientSession, Class<TResult> resultClass) {
        return db.listCollections(clientSession, resultClass);
    }

    @Override
    public void createCollection(String collectionName) {
        db.createCollection(collectionName);
    }

    @Override
    public void createCollection(String collectionName, CreateCollectionOptions createCollectionOptions) {
        db.createCollection(collectionName, createCollectionOptions);
    }

    @Override
    public void createCollection(ClientSession clientSession, String collectionName) {
        db.createCollection(clientSession, collectionName);
    }

    @Override
    public void createCollection(ClientSession clientSession, String collectionName, CreateCollectionOptions createCollectionOptions) {
        db.createCollection(clientSession, collectionName, createCollectionOptions);
    }

    @Override
    public void createView(String viewName, String viewOn, List<? extends Bson> pipeline) {
        db.createView(viewName, viewOn, pipeline);
    }

    @Override
    public void createView(String viewName, String viewOn, List<? extends Bson> pipeline, CreateViewOptions createViewOptions) {
        db.createView(viewName, viewOn, pipeline, createViewOptions);
    }

    @Override
    public void createView(ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline) {
        db.createView(clientSession, viewName, viewOn, pipeline);
    }

    @Override
    public void createView(ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline,
                           CreateViewOptions createViewOptions) {
        db.createView(clientSession, viewName, viewOn, pipeline, createViewOptions);
    }

    @Override
    public ChangeStreamIterable<Document> watch() {
        return db.watch();
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> resultClass) {
        return db.watch(resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
        return db.watch(pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> resultClass) {
        return db.watch(pipeline, resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
        return db.watch(clientSession);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> resultClass) {
        return db.watch(clientSession, resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> pipeline) {
        return db.watch(clientSession, pipeline);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> pipeline,
                                                         Class<TResult> resultClass) {
        return db.watch(clientSession, pipeline, resultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
        return db.aggregate(pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> resultClass) {
        return db.aggregate(pipeline, resultClass);
    }

    @Override
    public AggregateIterable<Document> aggregate(ClientSession clientSession, List<? extends Bson> pipeline) {
        return db.aggregate(clientSession, pipeline);
    }

    @Override
    public <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> pipeline,
                                                          Class<TResult> resultClass) {
        return db.aggregate(clientSession, pipeline, resultClass);
    }

}
