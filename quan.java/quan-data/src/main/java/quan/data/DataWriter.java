package quan.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 数据写入器，负责数据的插入、删除和更新
 */
public interface DataWriter {

    /**
     * 把数据更改写到数据库
     *
     * @param inserts 要插入的数据
     * @param deletes 要删除的数据
     * @param updates 要更新的数据和更新补丁，参考{@link Data#_getUpdatePatch()}
     */
    void write(Set<Data<?>> inserts, Set<Data<?>> deletes, Map<Data<?>, Map<String, Object>> updates);

    /**
     * 插入数据，参考{@link Data#insert(DataWriter)}
     */
    default void insert(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.insert(this));
    }

    /**
     * 插入数据，参考{@link Data#insert(DataWriter)}
     */
    default void insert(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.insert(this));
    }

    /**
     * 删除数据，参考{@link Data#delete(DataWriter)}
     */
    default void delete(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.delete(this));
    }

    /**
     * 删除数据，参考{@link Data#delete(DataWriter)}
     */
    default void delete(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.delete(this));
    }

    /**
     * 更新数据，参考{@link Data#update(DataWriter)}
     */
    default void update(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.update(this));
    }

    /**
     * 更新数据，参考{@link Data#update(DataWriter)}
     */
    default void update(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.update(this));
    }

}
