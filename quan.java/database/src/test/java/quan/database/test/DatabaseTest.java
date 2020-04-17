package quan.database.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
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

        testMongoClient();

//        testWrite();
//        testRead();

//        testMap();
    }


    private static void testMongoClient() throws Exception {

        MongoManager mongoManager = new MongoManager("mongodb://127.0.0.1:27017", "test", "quan");

//        Transaction.listenCommit(changes -> System.err.println("changes:" + changes));

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
        long startTime = System.nanoTime();
        Transaction.execute(() -> {
            doTestMongoCollection1(mongoManager);
            return true;
        });
        System.err.println("testMongoCollection1 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private static void doTestMongoCollection1(MongoManager mongoManager) {

        System.err.println("testMongoCollection1 start===========");
        MongoCollection<RoleData> roleDataCollection = mongoManager.getCollection(RoleData.class);

//        for (Document index : roleDataCollection.listIndexes()) {
//            System.err.println("index:" + new HashMap<>(index));
//        }

        long startTime, endTime;

//        startTime = System.nanoTime();
        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending("_id")).first();
//        endTime = System.nanoTime();
//        System.err.println("find costTime:" + (endTime - startTime) / timeBase);
//
//        System.err.println("roleDataMax:" + roleDataMax);

        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa");
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("aaaaa" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

//        startTime = System.nanoTime();
//        UpdateResult updateResult = roleDataCollection.replaceOne(Filters.eq(roleDataMax._id()), roleDataMax, new ReplaceOptions().upsert(true));
//        endTime = System.nanoTime();
//        System.err.println("replaceOne costTime:" + (endTime - startTime) / timeBase + ",updateResult:" + updateResult);

        RoleData roleData1 = new RoleData(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData roleData2 = new RoleData(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData roleData3 = new RoleData(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);
//        roleDataCollection.insertOne(roleData1);
//        roleDataCollection.insertOne(roleData2);
//        roleDataCollection.insertOne(roleData3);

//        startTime = System.nanoTime();
        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 10; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s" + j);
                roleData.getList2().add(new ItemEntity().setId((int) i).setName("item2-" + i));
            }
            roleDataList.add(roleData);
        }
//        endTime = System.nanoTime();
//        System.err.println("roleDataList costTime:" + (endTime - startTime) / timeBase);

//        startTime = System.nanoTime();
//        roleDataCollection.insertMany(roleDataList);
//        endTime = System.nanoTime();
//        System.err.println("insertMany costTime:" + (endTime - startTime) / timeBase);
//        Transaction.breakdown();
    }

    private static void testMongoCollection2(MongoManager mongoManager) {

        long testMongoCollection2StartTime = System.nanoTime();

        System.err.println("testMongoCollection2 start===========");

        MongoCollection<RoleData2> roleDataCollection2 = mongoManager.getDatabase().getCollection(RoleData2._NAME, RoleData2.class);

        long startTime, endTime;

//        startTime = System.nanoTime();
        RoleData2 roleDataMax = roleDataCollection2.find().sort(Sorts.descending("_id")).first();
//        endTime = System.nanoTime();
//        System.err.println("find costTime:" + (endTime - startTime) / timeBase);
//
//        System.err.println("roleDataMax:" + roleDataMax);

        if (roleDataMax == null) {
            roleDataMax = new RoleData2(1L);
            roleDataMax.setName("aaa");
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("aaaaa" + i);
            roleDataMax.getSet().add(i % 2 == 1);
        }

//        startTime = System.nanoTime();
//        UpdateResult updateResult = roleDataCollection2.replaceOne(Filters.eq(roleDataMax._getId()), roleDataMax, new ReplaceOptions().upsert(true));
//        endTime = System.nanoTime();
//        System.err.println("replaceOne costTime:" + (endTime - startTime) / timeBase + ",updateResult:" + updateResult);

        RoleData2 roleData1 = new RoleData2(roleDataMax.getId() + 1).setName("name:" + roleDataMax.getId() + 1);
        RoleData2 roleData2 = new RoleData2(roleDataMax.getId() + 2).setName("name:" + roleDataMax.getId() + 2);
        RoleData2 roleData3 = new RoleData2(roleDataMax.getId() + 3).setName("name:" + roleDataMax.getId() + 3);
        roleDataCollection2.insertOne(roleData1);
        roleDataCollection2.insertOne(roleData2);
        roleDataCollection2.insertOne(roleData3);

//        startTime = System.nanoTime();
        List<RoleData2> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleDataMax.getId() + 10; i++) {
            RoleData2 roleData = new RoleData2(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity2().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s" + j);
                roleData.getList2().add(new ItemEntity2().setId((int) i).setName("item2-" + i));
            }
            roleDataList.add(roleData);
        }
//        endTime = System.nanoTime();
//        System.err.println("roleDataList costTime:" + (endTime - startTime) / timeBase);

//        startTime = System.nanoTime();
        roleDataCollection2.insertMany(roleDataList);
//        endTime = System.nanoTime();
//        System.err.println("insertMany costTime:" + (endTime - startTime) / timeBase);

        System.err.println("testMongoCollection2 costTime:" + (System.nanoTime() - testMongoCollection2StartTime) / timeBase);
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
