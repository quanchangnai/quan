package quan.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 数据更新器，把数据修改更新到MongoDB
 * Created by quanchangnai on 2020/4/7.
 */
public class DataUpdater implements Consumer<Set<Data<?>>> {

    private Map<String, MongoCollection<Data<?>>> collections = new HashMap<>();

    private ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

    public DataUpdater(Map<String, MongoCollection<Data<?>>> collections) {
        this.collections.putAll(collections);
    }

    @Override
    public void accept(Set<Data<?>> changes) {
        for (Data<?> change : changes) {
            MongoCollection<Data<?>> collection = collections.get(change._name());
            if (collection != null) {
                collection.replaceOne(Filters.eq(change._id()), change, replaceOptions);
            }
        }
    }
}
