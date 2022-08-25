package quan.rpc.serialize;

/**
 * 序列化和反序列化时的对象类型
 *
 * @author quanchangnai
 */
public interface ObjectType {

    int NULL = 0;

    int BYTE = 1;

    int BYTE_ARRAY = 2;

    int BOOLEAN = 3;

    int BOOLEAN_ARRAY = 4;

    int SHORT = 5;

    int SHORT_ARRAY = 6;

    int INTEGER = 7;

    /**
     * @see java.util.OptionalInt
     */
    int OPTIONAL_INT = 8;

    int INT_ARRAY = 9;

    int LONG = 10;

    /**
     * @see java.util.OptionalLong
     */
    int OPTIONAL_LONG = 11;

    int LONG_ARRAY = 12;

    int FLOAT = 13;

    int FLOAT_ARRAY = 14;

    int DOUBLE = 15;

    /**
     * @see java.util.OptionalDouble
     */
    int OPTIONAL_DOUBLE = 16;

    int DOUBLE_ARRAY = 17;

    int STRING = 18;

    int STRING_ARRAY = 19;

    /**
     * Object本身，不包含其子类
     */
    int OBJECT = 20;

    /**
     * 除了原生类型数组之外的任意可序列化的对象数组
     */
    int OBJECT_ARRAY = 21;

    int ENUM = 22;

    /**
     * 除了ArrayList本身之外，没有特殊处理的集合子类型也会被反序列化成ArrayList
     */
    int ARRAY_LIST = 23;

    /**
     * SortedSet反序列化时使用TreeSet实现
     */
    int SORTED_SET = 24;

    int HASH_SET = 25;

    int LINKED_LIST = 26;

    int ARRAY_DEQUE = 27;

    /**
     * 除了HashMap本身之外，没有特殊处理的Map子类型也会被反序列化成HashMap
     */
    int HASH_MAP = 35;

    /**
     * SortedMap反序列化时使用TreeMap实现
     */
    int SORTED_MAP = 36;

    /**
     * RPC协议：{@link quan.rpc.protocol.Protocol}
     */
    int PROTOCOL = 40;

    /**
     * 除了RPC协议之外的{@link Transferable}对象
     */
    int TRANSFERABLE = 41;

    /**
     * 消息：{@link quan.message.Message}
     */
    int MESSAGE = 42;

    /**
     * 通过对象流序列化对象消耗的时间和占用的空间都很大，尽量不要使用<br/>
     * {@link java.io.Serializable}
     */
    int SERIALIZABLE = 43;

}
