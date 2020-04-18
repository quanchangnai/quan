package quan.database.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.database.MongoManager;
import quan.database.Transaction;
import quan.database.item.ItemEntity;
import quan.database.item.ItemEntity2;
import quan.database.role.RoleData;
import quan.database.role.RoleData2;

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

    public static void main(String[] args) throws Exception {

//        testMongoClient();

        test();

//        testWrite();
//        testRead();

//        testMap();


    }

    private static void testMongoClient() throws Exception {

        MongoManager mongoManager = new MongoManager("mongodb://127.0.0.1:27017", "test", "quan");

        for (int i = 0; i < 10; i++) {
            System.err.println("=============" + i);
            testMongoCollection1(mongoManager);
            System.err.println();
            testMongoCollection2(mongoManager);
            System.err.println();
        }


        mongoManager.getClient().close();

//        while (true) {
//            Thread.sleep(10000);
//        }
    }

    private static final double timeBase = 1000000D;

    private static void testMongoCollection1(MongoManager mongoManager) {
        System.err.println("testMongoCollection1 start===========");
        long startTime = System.nanoTime();
        Transaction.execute(() -> {
            doTestMongoCollection1(mongoManager);
            return true;
        });
        System.err.println("testMongoCollection1 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private static void doTestMongoCollection1(MongoManager mongoManager) {
        MongoCollection<RoleData> roleDataCollection = mongoManager.getCollection(RoleData.class);

        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending("_id")).first();

        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa");
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("aaaaa" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

        RoleData roleData1 = new RoleData(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData roleData2 = new RoleData(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData roleData3 = new RoleData(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);

        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 20; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s" + j);
                roleData.getList2().add(new ItemEntity().setId((int) i).setName("item2-" + i));
            }
            roleDataList.add(roleData);
        }

//        Transaction.breakdown();
    }

    private static void testMongoCollection2(MongoManager mongoManager) {
        System.err.println("testMongoCollection2 start===========");

        long startTime = System.nanoTime();

        MongoCollection<RoleData2> roleDataCollection2 = mongoManager.getDatabase().getCollection(RoleData2._NAME, RoleData2.class);


        RoleData2 roleDataMax = roleDataCollection2.find().sort(Sorts.descending("_id")).first();

        if (roleDataMax == null) {
            roleDataMax = new RoleData2(1L);
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("aaaaa" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

        UpdateResult updateResult = roleDataCollection2.replaceOne(Filters.eq(roleDataMax._getId()), roleDataMax, new ReplaceOptions().upsert(true));

        RoleData2 roleData1 = new RoleData2(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData2 roleData2 = new RoleData2(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData2 roleData3 = new RoleData2(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);
        roleDataCollection2.insertOne(roleData1);
        roleDataCollection2.insertOne(roleData2);
        roleDataCollection2.insertOne(roleData3);

        List<RoleData2> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 20; i++) {
            RoleData2 roleData = new RoleData2(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity2().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s" + j);
                roleData.getList2().add(new ItemEntity2().setId((int) i).setName("item2-" + i));
            }
            roleDataList.add(roleData);
        }

        roleDataList.forEach(roleDataCollection2::insertOne);

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
            return true;
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