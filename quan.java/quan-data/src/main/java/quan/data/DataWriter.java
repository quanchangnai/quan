package quan.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 负责把数据的插入、更新和删除操作写到到数据库
 */
public interface DataWriter {

    /**
     * 把数据更改写到数据库
     */
    void write(List<Data<?>> insertions, List<Data<?>> updates, List<Data<?>> deletions);


    /**
     * 插入数据被到数据库，参考{@link Data#insert(DataWriter)}
     */
    default void insert(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.insert(this));
    }

    /**
     * 插入数据被到数据库，参考{@link Data#insert(DataWriter)}
     */
    default void insert(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.insert(this));
    }

    /**
     * 更新数据到数据库，参考{@link Data#update(DataWriter)}
     */
    default void update(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.update(this));
    }

    /**
     * 更新数据到数据库，参考{@link Data#update(DataWriter)}
     */
    default void update(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.update(this));
    }

    /**
     * 从数据库中删除数据，参考{@link Data#delete(DataWriter)}
     */
    default void delete(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.delete(this));
    }

    /**
     * 从数据库中删除数据，参考{@link Data#delete(DataWriter)}
     */
    default void delete(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.delete(this));
    }

}
