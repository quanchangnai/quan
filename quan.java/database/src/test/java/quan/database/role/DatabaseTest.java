package quan.database.role;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import quan.database.DataCodecRegistry;

import java.io.FileReader;
import java.io.FileWriter;

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
        DataCodecRegistry dataCodecRegistry = new DataCodecRegistry();
        dataCodecRegistry.register("quan");
        MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder().codecRegistry(dataCodecRegistry);
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://127.0.0.1:27017", optionsBuilder);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase testDatabase = mongoClient.getDatabase("test");
        MongoCollection<RoleData> roleDataCollection = testDatabase.getCollection(RoleData.class.getSimpleName(), RoleData.class);

        RoleData roleData = new RoleData(111L);
        roleDataCollection.replaceOne(Filters.eq("_id", 111L), roleData);


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
