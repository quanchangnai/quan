package quan.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 负责把数据的插入、更新和删除操作写到到数据库<br/>
 * Created by quanchangnai on 2020/4/17.
 */
public interface DataWriter {

    /**
     * 把数据更改写到数据库
     */
    void write(List<Data<?>> insertions, List<Data<?>> updates, List<Data<?>> deletions);


    /**
     * 标记数据将被插入到数据库
     */
    default void insert(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.insert(this));
    }

    /**
     * 标记数据将被插入到数据库
     */
    default void insert(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.insert(this));
    }

    /**
     * 标记数据将被更新到数据库
     */
    default void update(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.update(this));
    }

    /**
     * 标记数据将被更新到数据库
     */
    default void update(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.update(this));
    }

    /**
     * 标记数据将从数据库中删除
     */
    default void delete(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.delete(this));
    }

    /**
     * 标记数据将从数据库中删除
     */
    default void delete(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.delete(this));
    }

}
