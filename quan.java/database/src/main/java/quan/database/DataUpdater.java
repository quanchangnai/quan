package quan.database;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 数据更新器，负责在事务提交后更新数据到数据库
 * Created by quanchangnai on 2020/4/17.
 */
public interface DataUpdater {

    /**
     * 在事务提交后执行更新
     *
     * @param updates
     */
    void doUpdate(List<Data<?>> updates);

    /**
     * 标记数据使用这个更新器更新
     */
    default void update(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.update(this));
    }

    /**
     * 标记数据使用这个更新器更新
     */
    default void update(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.update(this));
    }

}
