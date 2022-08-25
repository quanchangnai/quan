package quan.rpc.serialize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 可序列化对象注册表，参考{@link Transferable}
 *
 * @author quanchangnai
 */
public class TransferableRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, Supplier<Transferable>> id2Factories = new HashMap<>();

    private Map<Class<? extends Transferable>, Integer> class2Ids = new HashMap<>();

    private Map<Integer, Class<? extends Transferable>> id2Classes = new HashMap<>();

    public void register(int id, Class<? extends Transferable> clazz, Supplier<Transferable> factory) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(factory);
        if (id2Classes.putIfAbsent(id, clazz) == null) {
            class2Ids.put(clazz, id);
            id2Factories.put(id, factory);
        } else if (!class2Ids.containsKey(clazz)) {
            logger.error("{}和{}的ID冲突了：{}", id2Classes.get(id).getSimpleName(), clazz.getSimpleName(), id);
        }
    }

    /**
     * 通过类型ID创建{@link  Transferable}
     *
     * @see #getId(Class)
     */
    public Transferable create(int id) {
        Supplier<Transferable> factory = id2Factories.get(id);
        if (factory == null) {
            throw new RuntimeException(String.format("%s[%s]的工厂函数不存在", Transferable.class.getSimpleName(), id));
        }
        return factory.get();
    }

    /**
     * 获得{@link  Transferable}序列化时用于标记类型的ID
     */
    public int getId(Class<? extends Transferable> clazz) {
        Integer id = class2Ids.get(clazz);
        if (id == null) {
            throw new RuntimeException(String.format("%s的ID不存在", clazz.getSimpleName()));
        }
        return id;
    }

}
