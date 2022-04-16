package quan.rpc.serialize;

/**
 * @author quanchangnai
 */
public interface ObjectType {

    int NULL = 0;

    int BYTE = 2;

    int BYTE_ARRAY = 3;

    int BOOLEAN = 4;

    int BOOLEAN_ARRAY = 5;

    int SHORT = 6;

    int SHORT_ARRAY = 7;

    int INTEGER = 8;

    int INT_ARRAY = 9;

    int LONG = 10;

    int LONG_ARRAY = 11;

    int FLOAT = 12;

    int FLOAT_ARRAY = 13;

    int DOUBLE = 14;

    int DOUBLE_ARRAY = 15;

    int STRING = 16;

    /**
     * Object本身，不包含其子类
     */
    int OBJECT = 18;

    /**
     * 除了原生类型数组之外的任意可序列化的对象数组
     */
    int OBJECT_ARRAY = 19;

    int ENUM = 20;

    /**
     * 除了ArrayList本身之外，没有特殊处理的集合子类型也会被反序列化成ArrayList
     */
    int ARRAY_LIST = 21;

    /**
     * SortedSet反序列化时使用TreeSet实现
     */
    int SORTED_SET = 22;

    int HASH_SET = 23;

    int LINKED_LIST = 24;

    int ARRAY_DEQUE = 25;

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
    int MESSAGE = 52;

    /**
     * 通过对象流序列化对象消耗的时间和占用的空间都很大，尽量不要使用<br/>
     * {@link java.io.Serializable}
     */
    int SERIALIZABLE = 43;

}
