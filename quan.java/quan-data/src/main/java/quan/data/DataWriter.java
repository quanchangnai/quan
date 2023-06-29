package quan.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 负责把数据的插入、更新和删除操作写到到数据库
 */
public interface DataWriter {

    /**
     * 把数据更改写到数据库
     */
    void write(Set<Data<?>> saves, Set<Data<?>> deletes);

    /**
     * 保存数据到数据库，参考{@link Data#save(DataWriter)}
     */
    default void save(Data<?>... data) {
        Arrays.stream(data).forEach(d -> d.save(this));
    }

    /**
     * 保存数据到数据库，参考{@link Data#save(DataWriter)}
     */
    default void save(Collection<? extends Data<?>> data) {
        data.forEach(d -> d.save(this));
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
