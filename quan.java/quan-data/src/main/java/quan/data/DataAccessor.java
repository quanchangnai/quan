package quan.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 数据存取器，负责数据的查找、插入、删除和更新
 */
public interface DataAccessor {

    /**
     * 通过主键_id查询数据
     *
     * @param clazz 数据类
     * @param _id   主键值
     */
    <D extends Data<I>, I> D find(Class<D> clazz, I _id);

    /**
     * 通过指定条件查询数据
     *
     * @param clazz      数据类
     * @param conditions 条件，字段名：字段值
     */
    <D extends Data<?>> Iterable<D> find(Class<D> clazz, Map<String, Object> conditions);


    /**
     * 把数据更改写到数据库
     *
     * @param inserts 要插入的数据
     * @param deletes 要删除的数据
     * @param updates 要更新的数据和更新补丁，参考{@link Data#_getUpdatePatch()}
     */
    void write(Set<Data<?>> inserts, Set<Data<?>> deletes, Map<Data<?>, Map<String, Object>> updates);

    /**
     * 插入数据，参考{@link Data#insert(DataAccessor)}
     */
    default void insert(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.insert(this));
    }

    /**
     * 插入数据，参考{@link Data#insert(DataAccessor)}
     */
    default void insert(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.insert(this));
    }

    /**
     * 删除数据，参考{@link Data#delete(DataAccessor)}
     */
    default void delete(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.delete(this));
    }

    /**
     * 删除数据，参考{@link Data#delete(DataAccessor)}
     */
    default void delete(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.delete(this));
    }

    /**
     * 更新数据，参考{@link Data#update(DataAccessor)}
     */
    default void update(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.update(this));
    }

    /**
     * 更新数据，参考{@link Data#update(DataAccessor)}
     */
    default void update(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.update(this));
    }

}
