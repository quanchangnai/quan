package quan.rpc;

/**
 * @author quanchangnai
 */
interface ObjectType {

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
     * 对象数组，包含Integer[]、String[]、Object[]等
     */
    int OBJECT_ARRAY = 19;

    int ENUM = 20;

    /**
     * 除了ArrayList本身之外，没有明确指明的集合类型也会被标记
     */
    int ARRAY_LIST = 21;

    int TREE_SET = 22;

    int HASH_SET = 23;

    int LINKED_LIST = 24;

    int ARRAY_DEQUE = 25;

    /**
     * 除了HashMap本身之外，没有明确指明的Map类型也会被标记
     */
    int HASH_MAP = 35;

    int TREE_MAP = 36;

    /**
     * {@link Transferable}
     */
    int TRANSFERABLE = 40;

    /**
     * 消息：{@link quan.message.Message}
     */
    int MESSAGE = 41;

}
