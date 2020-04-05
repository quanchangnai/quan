package quan.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import quan.database.item.ItemEntity;
import quan.database.role.RoleData;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class DatabaseTest {

    public static void main(String[] args) throws Exception {

//        testWrite();
//        testRead();

        testMongoClient();

    }

    private static void testMongoClient() throws Exception {
        MongoClientSettings.Builder mongoClientSettings = MongoClientSettings.builder();
        mongoClientSettings.applyConnectionString(new ConnectionString("mongodb://127.0.0.1:27017"));
        mongoClientSettings.codecRegistry(new PackageCodecRegistry("quan"));
        MongoClient mongoClient = MongoClients.create(mongoClientSettings.build());

        MongoDatabase testDatabase = mongoClient.getDatabase("test");
        MongoCollection<RoleData> roleDataCollection = testDatabase.getCollection(RoleData._NAME, RoleData.class);

        long startTime, endTime;

        startTime = System.currentTimeMillis();
        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending("_id")).first();
        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa");
        }
        endTime = System.currentTimeMillis();
        System.err.println("find costTime:" + (endTime - startTime));

        RoleData roleData1 = new RoleData(111L);
        for (int i = 0; i < 20; i++) {
            roleData1.getList().add("aaaaa" + i);
            roleData1.getSet().add(true);
        }
        startTime = System.currentTimeMillis();
        UpdateResult updateResult = roleDataCollection.replaceOne(Filters.eq(111L), roleData1);
        endTime = System.currentTimeMillis();
        System.err.println("replaceOne costTime:" + (endTime - startTime) + ",updateResult:" + updateResult);

        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 1));
        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 2));
        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 3));

        startTime = System.currentTimeMillis();
        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleDataMax.getId() + 5; i < roleDataMax.getId() + 20; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 200; j++) {
                roleData.getList().add("s" + j);
                roleData.getList2().add(new ItemEntity().setId((int) i).setName("item2-" + i));
            }
            roleDataList.add(roleData);
        }

        endTime = System.currentTimeMillis();
        System.err.println("roleDataList costTime:" + (endTime - startTime));

        startTime = System.currentTimeMillis();

        roleDataCollection.insertMany(roleDataList);

        endTime = System.currentTimeMillis();
        System.err.println("insertMany costTime:" + (endTime - startTime));

        mongoClient.close();

//        while (true) {
//            Thread.sleep(10000);
//        }
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
