package quan.mongo;

import com.mongodb.MongoNamespace;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoClient;
import com.mongodb.operation.BatchCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Data;
import quan.data.DataWriter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by quanchangnai on 2020/4/22.
 */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
class Cursor implements BatchCursor {

    private static final Logger logger = LoggerFactory.getLogger(Cursor.class);

    private static BiConsumer<Data, DataWriter> setDataWriter;

    static {
        try {
            Field field = Data.class.getDeclaredField("_setWriter");
            field.setAccessible(true);
            setDataWriter = (BiConsumer<Data, DataWriter>) field.get(Data.class);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private Database database;

    private BatchCursor cursor;

    public Cursor(MongoClient client, MongoNamespace namespace, BatchCursor cursor) {
        if (Database.databases.containsKey(client)) {
            this.database = Database.databases.get(client).get(namespace.getDatabaseName());
        }
        this.cursor = cursor;
    }

    @Override
    public void close() {
        cursor.close();
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }

    @Override
    public List next() {
        List list = cursor.next();
        for (Object o : list) {
            if (o instanceof Data) {
                setDataWriter.accept((Data) o, database);
            }
        }
        return list;
    }

    @Override
    public void setBatchSize(int batchSize) {
        cursor.setBatchSize(batchSize);
    }

    @Override
    public int getBatchSize() {
        return cursor.getBatchSize();
    }

    @Override
    public List<?> tryNext() {
        return cursor.tryNext();
    }

    @Override
    public ServerCursor getServerCursor() {
        return cursor.getServerCursor();
    }

    @Override
    public ServerAddress getServerAddress() {
        return cursor.getServerAddress();
    }

}
