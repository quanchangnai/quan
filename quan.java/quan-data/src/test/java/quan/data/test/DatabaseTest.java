package quan.data.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PVector;
import quan.data.Data;
import quan.data.Index;
import quan.data.Transaction;
import quan.data.EntityCodecProvider;
import quan.data.item.ItemBean;
import quan.data.mongo.Database;
import quan.data.role.RoleData;
import quan.util.ClassUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class DatabaseTest {

    static {
        ClassUtils.initAop();
    }

    @Test
    public void testRoleData() {
        Index.List indexes = RoleData.class.getAnnotation(Index.List.class);
        System.err.println("RoleData indexes:" + Arrays.toString(indexes.value()));

        EntityCodecProvider entityCodecProvider = new EntityCodecProvider();
        Codec<RoleData> roleDataCodec = entityCodecProvider.get(RoleData.class, null);
        System.err.println("roleDataCodec:" + roleDataCodec);
    }

    @Test
    public void testEncode() {
        Role role = new Role(1L);
        role.test1();
        String json = role.getRoleData1().toJson();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            json = role.getRoleData1().toJson();
        }
        long endTime = System.currentTimeMillis();
        System.err.println("testEncode costTime:" + (endTime - startTime));
        System.err.println("testEncode json:" + json);
    }


    @Test
    public void testRole() {
        System.err.println("testRole=============");
        Transaction.setLocalOptional(true);

        Role role = new Role(1L);
        role.test1();
        role.test2();
        role.reset();
        System.err.println();

        int n = 1000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
//            role.reset();
            role.test1();
        }
        long endTime = System.currentTimeMillis();
        System.err.println("role.test1 costTime:" + (endTime - startTime));


        startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
//            role.reset();
            role.test2();
        }
        endTime = System.currentTimeMillis();
        System.err.println("role.test2 costTime:" + (endTime - startTime));

        startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
//            role.reset();
        }
        endTime = System.currentTimeMillis();
        System.err.println("role.test3 costTime:" + (endTime - startTime));

        System.err.println();

        role.print();
    }

    //测试内嵌事务
    @Test
    public void testNested() {
        Role role = new Role(1L);
        try {
            role.test4();
        } finally {
            System.err.println("" + role.getRoleData1());
        }
    }

    @Test
    public void testCollection() {
        Transaction.run(this::testCollection0);
    }

    public void testCollection0() {
        Random random = new Random();

        PVector<String> list1 = Empty.vector();
        PMap<Integer, Integer> map1 = Empty.map();
        PMap<Integer, ItemBean> items1 = Empty.map();

        List<String> list2 = new ArrayList<>();
        Map<Integer, Integer> map2 = new HashMap<>();
        Map<Integer, ItemBean> items2 = new HashMap<>();

        int n = 10000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 10; j++) {
                list1 = list1.plus("aaa" + j);
                map1 = map1.plus(j, j + random.nextInt());
                items1 = items1.plus(j, new ItemBean(i, "item" + j, new ArrayList<>()));
            }

            for (String s : list1) {
                if (s.equals("aaa5")) {
                }
            }
            for (Integer k : map1.keySet()) {
                if (k == 5) {
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.err.println("testCollection1 costTime:" + (endTime - startTime));

        startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 10; j++) {
                list2.add("aaa" + j);
                map2.put(j, j + random.nextInt());
                items2.put(j, new ItemBean(j, "item" + j, new ArrayList<>()));
            }

            for (String s : list2) {
                if (s.equals("aaa5")) {
                }
            }
            for (Integer k : map2.keySet()) {
                if (k == 5) {
                }
            }
        }

        endTime = System.currentTimeMillis();
        System.err.println("testCollection2 costTime:" + (endTime - startTime));

        System.err.println("list.size:");
        System.err.println(list1.size());
        System.err.println(list2.size());
    }

    @Ignore
    @Test
    public void testDatabase() {
        Transaction.setGlobalOptional(true);
        System.err.println("testDatabase==============");
        String connectionString = "mongodb://127.0.0.1:27017";
        Database database = new Database(connectionString, "test", "quan.data");
        Data._setDefaultAccessor(database);

        try {
            database.getExecutor().submit(() -> testDatabase0(database)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testDatabase0(Database database) {
        testMongoCollection1(database);

        testMongoCollection2(database);

//        ChangeStreamIterable<RoleData> changeStream = database.getCollection(RoleData.class).watch(RoleData.class);
//        MongoChangeStreamCursor<ChangeStreamDocument<RoleData>> cursor = changeStream.cursor();
//        while (cursor.hasNext()) {
//            System.err.println("cursor.next():" + cursor.next());
//        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        database.getClient().close();
    }

    private static final double timeBase = 1000000D;

    private void testMongoCollection1(Database database) {
        System.err.println("\ntestMongoCollection1===========");
        Transaction.run(() -> {
            MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);
            RoleData roleData = roleDataCollection.find().first();
            if (roleData != null) {
                System.err.println("roleData:" + roleData);
                roleData.delete(database);
                roleData.insert(database);
//                roleData.free();
                long currentTime = System.currentTimeMillis();
                roleData.setName("name:" + currentTime);
                roleData.setName2("name2:" + currentTime);
                if (!roleData.getList2().isEmpty()) {
                    roleData.getList2().get(0).setName("道具:" + currentTime);
                }
            }
        });
    }

    private void testMongoCollection2(Database database) {
        System.err.println("\ntestMongoCollection2 start===========");
        long startTime = System.nanoTime();
        Transaction.run(() -> doTestMongoCollection2(database));
        System.err.println("testMongoCollection2 costTime:" + (System.nanoTime() - startTime) / timeBase);
    }

    private void doTestMongoCollection2(Database database) {
        MongoCollection<RoleData> roleDataCollection = database.getCollection(RoleData.class);

        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending(RoleData._ID)).first();
        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("张三:a");
            roleDataMax.setName2("张三:b");
            roleDataMax.insert(database);
        } else {
            roleDataMax.setName("张三:" + System.nanoTime());
            roleDataMax.setName2(roleDataMax.getName() + ":2");
        }

        for (int i = 0; i < 20; i++) {
            roleDataMax.getList().add("name:" + i);
//            roleDataMax.getSet().add(i % 2 == 1);
        }

        RoleData roleData1 = new RoleData(roleDataMax.getId() + 1)
                .setName("name:" + roleDataMax.getId() + 1)
                .setName2("name2:" + roleDataMax.getId() + 1);
        RoleData roleData2 = new RoleData(roleDataMax.getId() + 2)
                .setName("name:" + roleDataMax.getId() + 2)
                .setName2("发射 火箭:" + roleDataMax.getId() + 1);
        RoleData roleData3 = new RoleData(roleDataMax.getId() + 3)
                .setName("name:" + roleDataMax.getId() + 3)
                .setName2("name2:" + roleDataMax.getId() + 1);

        database.insert(roleData1, roleData2, roleData3);

        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleData3.getId() + 1; i < roleData3.getId() + 20; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa:" + i);
            roleData.setB(i % 2 == 0);
            roleData.setItem(new ItemBean().setId((int) i).setName("item:" + i));
            for (long j = i; j < i + 20; j++) {
                roleData.getList().add("s:" + j);
                roleData.getList2().add(new ItemBean().setId((int) i).setName("item2:" + i));
            }
            roleDataList.add(roleData);
            roleData.insert(database);
        }

        database.insert(roleDataList);

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
