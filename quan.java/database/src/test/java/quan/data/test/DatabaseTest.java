package quan.data.test;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.common.AspectUtils;
import quan.data.Transaction;
import quan.data.item.ItemEntity;
import quan.data.item.ItemEntity2;
import quan.data.role.RoleData;
import quan.data.role.RoleData2;
import quan.mongo.Database;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class DatabaseTest {

    static {
        AspectUtils.enable();
    }


    public static void main(String[] args) throws Exception {

//        test();

//        testWrite();
//        testRead();

        testRole();

//        testMongoClient();

//        testMap();


    }

    private static void testRole() throws Exception {
        System.err.println("roleTest1=============");
        Role roleTest1 = new Role(123L);
        System.err.println("roleTest1.test1():" + roleTest1.test1(111));
        roleTest1.test2();

        System.err.println("roleTest2=============");
        Role roleTest2 = new Role(Long.valueOf(234L));
        System.err.println("roleTest2.test1():" + roleTest2.test1(222));
        roleTest1.test2();
    }

    private static void testMongoClient() throws Exception {

        Database database = new Database("mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019", "test", "quan.data");

        testMongoCollection0(database);

        for (int i = 0; i < 10; i++) {
            System.err.println("=============" + i);
            testMongoCollection1(database);
            System.err.println();
            testMongoCollection2(database);
            System.err.println();
        }

        ChangeStreamIterable<RoleData> changeStream = database.getCollection(RoleData.class).watch(RoleData.class);
        MongoChangeStreamCursor<ChangeStreamDocument<RoleData>> cursor = changeStream.cursor();
        while (cursor.hasNext()) {
            System.err.println("cursor.next():" + cursor.next());
        }

//        mongo.getClient().close();

        while (true) {
            Thread.sleep(10000);
        }
    }

    private static final double timeBase = 1000000D;

    private static void testMongoCollection0(Database database) {
        Transaction.execute(() -> {
            MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);
            RoleData roleData = roleDataCollection.find().first();
//            roleData.delete(mongo);
            roleData.free();
            roleData.setName("name:" + System.currentTimeMillis());
        });
    }

    private static void testMongoCollection1(Database database) {
        System.err.println("testMongoCollection1 start===========");
        long startTime = System.nanoTime();
        Transaction.execute(() -> doTestMongoCollection1(database));
        System.err.println("testMongoCollection1 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private static void doTestMongoCollection1(Database database) {
        MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);

        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending(RoleData._ID)).first();

        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa:1");
            roleDataMax.insert(database);
        } else {
            roleDataMax.setName("max:" + System.nanoTime());
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

    private static void testMongoCollection2(Database database) {
        System.err.println("testMongoCollection2 start===========");

        long startTime = System.nanoTime();

        MongoCollection<RoleData2> roleDataCollection2 = database.getCollection(RoleData2._NAME, RoleData2.class);


        RoleData2 roleDataMax = roleDataCollection2.find().sort(Sorts.descending("_id")).first();

        if (roleDataMax == null) {
            roleDataMax = new RoleData2(1L);
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("name:" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

        UpdateResult updateResult = roleDataCollection2.replaceOne(Filters.eq(roleDataMax._getId()), roleDataMax, new ReplaceOptions().upsert(true));

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

    private static void test() {
        for (int i = 0; i < 10; i++) {
            System.err.println("=============" + i);
            test2();
            System.err.println();
            test1();
            System.err.println();
        }
    }


    private static void test1() {
        System.err.println("test1 start===========");

        Transaction.execute(() -> {
            long startTime = System.nanoTime();

            List<RoleData> roleDataList = new ArrayList<>();
            for (long i = 1; i < 20; i++) {
                RoleData roleData = new RoleData(i);
//                roleData.setName("aaa" + i);
//                roleData.setItem(new ItemEntity().setId((int) i).setName("item" + i));
//                for (long j = i; j < i + 20; j++) {
//                    roleData.getList().add("s" + j);
//                    roleData.getList2().add(new ItemEntity().setId((int) i).setName("item2-" + i));
//                }
                roleDataList.add(roleData);
            }

            System.err.println("roleDataList costTime:" + (System.nanoTime() - startTime) / timeBase);
        });

    }

    private static void test2() {
        System.err.println("test2 start===========");

        long startTime = System.nanoTime();
        List<RoleData2> roleDataList = new ArrayList<>();
        for (long i = 1; i < 20; i++) {
            RoleData2 roleData = new RoleData2(i);
//            roleData.setName("aaa" + i);
//            roleData.setItem(new ItemEntity2().setId((int) i).setName("item" + i));
//            for (long j = i; j < i + 20; j++) {
//                roleData.getList().add("s" + j);
//                roleData.getList2().add(new ItemEntity2().setId((int) i).setName("item2-" + i));
//            }
            roleDataList.add(roleData);
        }
        System.err.println("roleDataList costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private static void testMap() {
        System.err.println("testMap()=================");
        long startTime, endTime;

        startTime = System.nanoTime();
        Map<Integer, String> map1 = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map1.put(i, "map1_" + i);
        }
        endTime = System.nanoTime();
        System.err.println("map1.put costTime:" + (endTime - startTime));

        startTime = System.nanoTime();
        PMap<Integer, String> map2 = Empty.map();
        for (int i = 0; i < 90; i++) {
            map2 = map2.plus(i, "map2_" + i);
        }
        endTime = System.nanoTime();
        System.err.println("map2.plus1 costTime:" + (endTime - startTime));

        startTime = System.nanoTime();
        for (int i = 90; i < 100; i++) {
            map2 = map2.plus(i, "map2_" + i);
        }
        endTime = System.nanoTime();
        System.err.println("map2.plus2 costTime:" + (endTime - startTime));

        startTime = System.nanoTime();
        Map<Integer, String> map3 = new HashMap<>();
        map3.putAll(map1);
        endTime = System.nanoTime();
        System.err.println("map3.putAll costTime:" + (endTime - startTime));

        startTime = System.nanoTime();
        for (Integer i : map1.keySet()) {
            String s = map1.get(i);
        }
        endTime = System.nanoTime();
        System.err.println("map1.for costTime:" + (endTime - startTime));

        startTime = System.nanoTime();
        for (Integer i : map2.keySet()) {
            String s = map1.get(i);
        }
        endTime = System.nanoTime();
        System.err.println("map2.for costTime:" + (endTime - startTime));

    }

    private static void testWrite() throws Exception {
        FileWriter fileWriter = new FileWriter("E://test.json");
        BsonWriter bsonWriter = new JsonWriter(fileWriter);
        bsonWriter.writeStartDocument();

        bsonWriter.writeInt32("aaa", 111);

        bsonWriter.writeStartDocument("bbb");

        bsonWriter.writeEndDocument();

        bsonWriter.writeEndDocument();

        fileWriter.close();

    }

    private static void testRead() throws Exception {
        BsonReader bsonReader = new JsonReader(new FileReader("E://test.json"));

        bsonReader.readStartDocument();

        System.err.println(bsonReader.readInt32("aaa"));

        bsonReader.readName();

        bsonReader.readStartDocument();
        bsonReader.readEndDocument();

        bsonReader.readEndDocument();
    }
}
