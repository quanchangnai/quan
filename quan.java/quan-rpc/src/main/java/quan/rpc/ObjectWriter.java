package quan.rpc;

import quan.message.CodedBuffer;
import quan.message.Message;

import java.util.*;

import static quan.rpc.ObjectType.*;

/**
 * @author quanchangnai
 */
public class ObjectWriter {

    private CodedBuffer buffer;

    public ObjectWriter(CodedBuffer buffer) {
        this.buffer = Objects.requireNonNull(buffer);
    }

    public void write(Object value) {
        if (value == null) {
            buffer.writeInt(NULL);
            return;
        }

        Class<?> clazz = value.getClass();
        if (clazz == Byte.class) {
            buffer.writeInt(BYTE);
            buffer.writeByte((Byte) value);
        } else if (clazz == byte[].class) {
            buffer.writeInt(BYTE_ARRAY);
            buffer.writeBytes((byte[]) value);
        } else if (clazz == Boolean.class) {
            buffer.writeInt(BOOLEAN);
            buffer.writeBool((Boolean) value);
        } else if (clazz == boolean[].class) {
            write((boolean[]) value);
        } else if (clazz == Short.class) {
            buffer.writeInt(SHORT);
            buffer.writeShort((Short) value);
        } else if (clazz == short[].class) {
            write((short[]) value);
        } else if (clazz == Integer.class) {
            buffer.writeInt(INTEGER);
            buffer.writeInt((Integer) value);
        } else if (clazz == int[].class) {
            write((int[]) value);
        } else if (clazz == Long.class) {
            buffer.writeInt(LONG);
            buffer.writeLong((Long) value);
        } else if (clazz == long[].class) {
            write((long[]) value);
        } else if (clazz == Float.class) {
            buffer.writeInt(FLOAT);
            buffer.writeFloat((Float) value);
        } else if (clazz == float[].class) {
            write((float[]) value);
        } else if (clazz == Double.class) {
            buffer.writeInt(DOUBLE);
            buffer.writeDouble((Double) value);
        } else if (clazz == double[].class) {
            write((double[]) value);
        } else if (clazz == String.class) {
            buffer.writeInt(STRING);
            buffer.writeString((String) value);
        } else if (clazz == Object.class) {
            buffer.writeInt(OBJECT);
        } else if (value instanceof Object[]) {
            write((Object[]) value);
        } else if (value instanceof Enum) {
            write((Enum<?>) value);
        } else if (value instanceof Collection) {
            write((Collection<?>) value);
        } else if (value instanceof Map) {
            write((Map<?, ?>) value);
        } else if (value instanceof Transferable) {
            buffer.writeInt(TRANSFERABLE);
            buffer.writeInt(clazz.getName().hashCode());
            ((Transferable) value).transferTo(this);
        } else if (value instanceof Message) {
            buffer.writeInt(MESSAGE);
            ((Message) value).encode(buffer);
        } else {
            throw new RuntimeException("不支持的数据类型:" + clazz);
        }
    }

    private void write(boolean[] array) {
        buffer.writeInt(BOOLEAN_ARRAY);
        buffer.writeInt(array.length);
        for (boolean v : array) {
            buffer.writeBool(v);
        }
    }

    private void write(short[] array) {
        buffer.writeInt(SHORT_ARRAY);
        buffer.writeInt(array.length);
        for (short v : array) {
            buffer.writeShort(v);
        }
    }

    private void write(int[] array) {
        buffer.writeInt(INT_ARRAY);
        buffer.writeInt(array.length);
        for (int v : array) {
            buffer.writeInt(v);
        }
    }

    private void write(long[] array) {
        buffer.writeInt(LONG_ARRAY);
        buffer.writeInt(array.length);
        for (long v : array) {
            buffer.writeLong(v);
        }
    }

    private void write(float[] array) {
        buffer.writeInt(FLOAT_ARRAY);
        buffer.writeInt(array.length);
        for (float v : array) {
            buffer.writeFloat(v);
        }
    }

    private void write(double[] array) {
        buffer.writeInt(DOUBLE_ARRAY);
        buffer.writeInt(array.length);
        for (double v : array) {
            buffer.writeDouble(v);
        }
    }

    private void write(Object[] array) {
        buffer.writeInt(OBJECT_ARRAY);
        buffer.writeInt(array.length);
        for (Object v : array) {
            write(v);
        }
    }

    private void write(Enum<?> value) {
        buffer.writeInt(ENUM);
        write(value.getDeclaringClass().getName());
        write(value.name());
    }

    private void write(Collection<?> collection) {
        int type;
        if (collection instanceof TreeSet) {
            type = TREE_SET;
            Object first = ((TreeSet<?>) collection).first();
            if (first != null && !(first instanceof Comparable)) {
                //TreeSet的元素没有实现Comparable时当做HashSet处理
                type = HASH_SET;
            }
        } else if (collection instanceof Set) {
            type = HASH_SET;
        } else if (collection instanceof LinkedList) {
            type = LINKED_LIST;
        } else if (collection instanceof ArrayDeque) {
            type = ARRAY_DEQUE;
        } else {
            //其他集合类型都当做ArrayList处理
            type = ARRAY_LIST;
        }

        buffer.writeInt(type);
        buffer.writeInt(collection.size());
        collection.forEach(this::write);
    }

    private void write(Map<?, ?> map) {
        int type;
        if (map instanceof TreeMap) {
            type = TREE_MAP;
            Object firstKey = ((TreeMap<?, ?>) map).firstKey();
            if (firstKey != null && !(firstKey instanceof Comparable)) {
                //TreeMap的key没有实现Comparable时当做HashMap处理
                type = HASH_MAP;
            }
        } else {
            //其他Map类型都当做HashMap处理
            type = HASH_MAP;
        }

        buffer.writeInt(type);
        buffer.writeInt(map.size());
        map.forEach((k, v) -> {
            write(k);
            write(v);
        });
    }

}
