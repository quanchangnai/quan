package quan.data.mongo;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.internal.operation.BatchCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Data;
import quan.data.DataAccessor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings({"unchecked", "rawtypes", "NullableProblems"})
public class Cursor implements BatchCursor {

    private static final Logger logger = LoggerFactory.getLogger(Cursor.class);

    private static BiConsumer<Data, DataAccessor> setDataAccessor;

    static {
        try {
            Field field = Data.class.getDeclaredField("_setAccessor");
            field.setAccessible(true);
            setDataAccessor = (BiConsumer<Data, DataAccessor>) field.get(Data.class);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private Database database;

    private BatchCursor cursor;

    public Cursor(Database database, BatchCursor cursor) {
        this.database = database;
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
        if (database != null) {
            list.stream().filter(d -> d instanceof Data).forEach(d -> setDataAccessor.accept((Data) d, database));
        }
        return list;
    }

    @Override
    public int available() {
        return cursor.available();
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
