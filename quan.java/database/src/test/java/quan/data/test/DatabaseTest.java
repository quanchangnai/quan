package quan.data.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.junit.Ignore;
import org.junit.Test;
import quan.common.utils.ClassUtils;
import quan.data.Index;
import quan.data.Transaction;
import quan.data.item.ItemEntity;
import quan.data.item.ItemEntity2;
import quan.data.mongo.CodecsRegistry;
import quan.data.role.RoleData;
import quan.data.role.RoleData2;
import quan.data.mongo.Database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class DatabaseTest {

    static {
        ClassUtils.enableAop();
    }

    @Test
    public void testRoleData() {
        Index.List indexes = RoleData.class.getAnnotation(Index.List.class);
        System.err.println("RoleData indexes:" + Arrays.toString(indexes.value()));

        CodecsRegistry codecsRegistry = new CodecsRegistry();
        Codec<RoleData> roleDataCodec = codecsRegistry.get(RoleData.class);
        System.err.println("roleDataCodec:" + roleDataCodec);
    }

    @Test
    public void testRole() {
        System.err.println("testRole=============");
        Role roleTest1 = new Role(123L);
        System.err.println("roleTest1.test1():" + roleTest1.test1(111));
        roleTest1.test2();

        Role roleTest2 = new Role(Long.valueOf(234L));
        System.err.println("roleTest2.test1():" + roleTest2.test1(222));
        roleTest1.test2();
    }

    @Ignore
    @Test
    public void testDatabase() {
        System.err.println("testDatabase==============");
        String connectionString = "mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019";
        Database database = new Database(connectionString, "test", "quan.data", true);

        testMongoCollection0(database);

        for (int i = 0; i < 1; i++) {
            System.err.println("=============" + i);
            testMongoCollection1(database);
            System.err.println();
            testMongoCollection2(database);
            System.err.println();
        }

//        ChangeStreamIterable<RoleData> changeStream = database.getCollection(RoleData.class).watch(RoleData.class);
//        MongoChangeStreamCursor<ChangeStreamDocument<RoleData>> cursor = changeStream.cursor();
//        while (cursor.hasNext()) {
//            System.err.println("cursor.next():" + cursor.next());
//        }

        database.getClient().close();
    }

    private static final double timeBase = 1000000D;

    private void testMongoCollection0(Database database) {
        Transaction.execute(() -> {
            MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);
            RoleData roleData = roleDataCollection.find().first();
            if (roleData != null) {
//                roleData.delete(database);
                roleData.free();
                roleData.setName("name:" + System.currentTimeMillis());
            }
        });
    }

    private void testMongoCollection1(Database database) {
        System.err.println("testMongoCollection1 start===========");
        long startTime = System.nanoTime();
        Transaction.execute(() -> doTestMongoCollection1(database));
        System.err.println("testMongoCollection1 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private void doTestMongoCollection1(Database database) {
        MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);

        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending(RoleData._ID)).first();
        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa:1");
            roleDataMax.insert(database);
        } else {
            roleDataMax.setName("max:" + System.nanoTime());
            System.err.println("roleDataMax:" + roleDataMax.toJson());

//            roleDataCollection.insertOne(roleDataMax);
//            roleDataCollection.deleteOne(Filters.eq(roleDataMax.getId()));
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("name:" + i);
//            roleDataMax.getSet().add(i % 2 == 1);
        }

        RoleData roleData1 = new RoleData(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData roleData2 = new RoleData(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData roleData3 = new RoleData(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);

        database.insert(roleData1, roleData2, roleData3);

        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 20; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa:" + i);
            roleData.setB(i % 2 == 0);
            roleData.setItem(new ItemEntity().setId((int) i).setName("item:" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s:" + j);
                roleData.getList2().add(new ItemEntity().setId((int) i).setName("item2:" + i));
            }
            roleDataList.add(roleData);
            roleData.update(database);
        }

        database.insert(roleDataList);

//        Transaction.breakdown();
    }

    private void testMongoCollection2(Database database) {
        System.err.println("testMongoCollection2 start===========");

        long startTime = System.nanoTime();

        MongoCollection<RoleData2> roleDataCollection2 = database.getCollection(RoleData2._NAME, RoleData2.class);

        RoleData2 roleDataMax = roleDataCollection2.find().sort(Sorts.descending("_id")).first();
        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData2(1L);
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("name:" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

        roleDataCollection2.replaceOne(Filters.eq(roleDataMax._getId()), roleDataMax, new ReplaceOptions().upsert(true));

        List<RoleData2> roleDataList = new ArrayList<>();

        RoleData2 roleData1 = new RoleData2(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData2 roleData2 = new RoleData2(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData2 roleData3 = new RoleData2(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);

        roleDataList.add(roleData1);
        roleDataList.add(roleData2);
        roleDataList.add(roleData3);

        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 20; i++) {
            RoleData2 roleData = new RoleData2(i);
            roleData.setName("aaa:" + i);
            roleData.setItem(new ItemEntity2().setId((int) i).setName("item:" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s:" + j);
                roleData.getList2().add(new ItemEntity2().setId((int) i).setName("item2:" + i));
            }
            roleDataList.add(roleData);
        }

//        ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);
//        List<WriteModel<RoleData2>> writeModels = new ArrayList<>();
//        for (RoleData2 data2 : roleDataList) {
//            ReplaceOneModel replaceOneModel = new ReplaceOneModel(Filters.eq(data2._getId()), data2, replaceOptions);
//            writeModels.add(replaceOneModel);
//        }
//        roleDataCollection2.bulkWrite(writeModels);

        roleDataCollection2.insertMany(roleDataList);

        System.err.println("testMongoCollection2 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    @Test
    public void testWriteBson() throws Exception {
        System.err.println("testWriteBson==========");

        File outPath = new File("build" + File.separator + "tmp");
        if (!outPath.mkdir()) {
            System.err.println("outPath.mkdir():false");
        }
        File file = new File(outPath, "test.json");

        FileWriter fileWriter = new FileWriter(file);
        BsonWriter bsonWriter = new JsonWriter(fileWriter);

        bsonWriter.writeStartDocument();
        bsonWriter.writeInt32("aaa", 111);
        bsonWriter.writeStartDocument("bbb");
        bsonWriter.writeEndDocument();
        bsonWriter.writeEndDocument();

        fileWriter.close();
    }

    @Test
    public void testReadBson() throws Exception {
        System.err.println("testReadBson==========");

        BsonReader bsonReader = new JsonReader(new FileReader("build" + File.separator + "tmp" + File.separator + "test.json"));

        bsonReader.readStartDocument();
        System.err.println(bsonReader.readInt32("aaa"));
        bsonReader.readName();
        bsonReader.readStartDocument();
        bsonReader.readEndDocument();
        bsonReader.readEndDocument();

        bsonReader.close();
    }

}
