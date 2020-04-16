package quan.database.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import quan.database.Data;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * MongoDB数据更新器
 * Created by quanchangnai on 2020/4/7.
 */
public class MongoUpdater implements Consumer<Set<Data<?>>> {

    private MongoManager manager;

    private ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    MongoUpdater(MongoManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @Override
    public void accept(Set<Data<?>> changes) {
        for (Data<?> data : changes) {
            MongoCollection collection = manager.getCollection(data.getClass());
            if (collection != null) {
                collection.replaceOne(Filters.eq(data._id()), data, replaceOptions);
            }
        }
    }
}
