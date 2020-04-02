package quan.database.role;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import quan.database.DataCodecRegistry;
import quan.database.item.ItemEntity;

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
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry("quan");
        MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder().codecRegistry(dataCodecRegistry);
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://127.0.0.1:27017", optionsBuilder);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase testDatabase = mongoClient.getDatabase("test");
        MongoCollection<RoleData> roleDataCollection = testDatabase.getCollection(RoleData.class.getSimpleName(), RoleData.class);

        RoleData roleDataMax = roleDataCollection.find().sort(Sorts.descending("_id")).first();
        System.err.println("roleDataMax:" + roleDataMax);
        if (roleDataMax == null) {
            roleDataMax = new RoleData(1L);
            roleDataMax.setName("aaa");
        }

        RoleData roleData1 = new RoleData(111L);
        UpdateResult updateResult = roleDataCollection.replaceOne(Filters.eq(111L), roleData1);
        System.err.println("updateResult:" + updateResult);

        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 1));
        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 2));
        roleDataCollection.insertOne(new RoleData(roleDataMax.getId() + 3));

        List<RoleData> roleDataList = new ArrayList<>();
        for (long i = roleDataMax.getId() + 10; i < roleDataMax.getId() + 100; i++) {
            RoleData roleData = new RoleData(i);
            roleData.setName("aaa" + i);
            roleData.setItem(new ItemEntity().setId((int) i).setName("item" + i));
            for (long j = i; j < i + 10; j++) {
                roleData.getList().add("s" + j);
            }
            roleDataList.add(roleData);
        }

        long startTime = System.currentTimeMillis();

        roleDataCollection.insertMany(roleDataList);

        long endTime = System.currentTimeMillis();
        System.err.println("insertMany costTime:" + (endTime - startTime));

        mongoClient.close();
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
