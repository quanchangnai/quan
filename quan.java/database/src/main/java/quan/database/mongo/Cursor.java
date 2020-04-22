package quan.database.mongo;

import com.mongodb.MongoNamespace;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoClient;
import com.mongodb.operation.BatchCursor;
import quan.database.Data;

import java.util.List;

/**
 * Created by quanchangnai on 2020/4/22.
 */
class Cursor implements BatchCursor {

    private Mongo mongo;

    private BatchCursor batchCursor;

    public Cursor(MongoClient mongoClient, MongoNamespace mongoNamespace, BatchCursor batchCursor) {
        this.mongo = Mongo.get(mongoClient, mongoNamespace.getDatabaseName());
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
        for (Object result : list) {
            if (result instanceof Data) {
                Data data = (Data) result;
                data.update(mongo);
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
    public List tryNext() {
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
