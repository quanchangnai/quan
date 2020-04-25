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

    private BatchCursor batchCursor;

    public Cursor(MongoClient mongoClient, MongoNamespace mongoNamespace, BatchCursor batchCursor) {
        this.database = Database.get(mongoClient, mongoNamespace.getDatabaseName());
        this.batchCursor = batchCursor;
    }

    @Override
    public void close() {
        batchCursor.close();
    }

    @Override
    public boolean hasNext() {
        return batchCursor.hasNext();
    }

    @Override
    public List next() {
        List list = batchCursor.next();
        for (Object o : list) {
            if (o instanceof Data) {
                setDataWriter.accept((Data) o, database);
            }
        }
        return list;
    }

    @Override
    public void setBatchSize(int batchSize) {
        batchCursor.setBatchSize(batchSize);
    }

    @Override
    public int getBatchSize() {
        return batchCursor.getBatchSize();
    }

    @Override
    public List<?> tryNext() {
        return batchCursor.tryNext();
    }

    @Override
    public ServerCursor getServerCursor() {
        return batchCursor.getServerCursor();
    }

    @Override
    public ServerAddress getServerAddress() {
        return batchCursor.getServerAddress();
    }

}
