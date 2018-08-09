package quan.mongo;

/**
 * Byte包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class ByteWrapper{

    //当前值
    private byte current;

    //原始值
    private byte origin;

    public ByteWrapper() {
    }

    public ByteWrapper(byte value) {
        this.current = value;
        this.origin = value;
    }

    public byte set(byte value) {
        byte ret = this.current;
        this.current = value;
        return ret;
    }

    public byte get() {
        return this.current;
    }

    public void commit() {
        this.origin = current;
    }

    public void rollback() {
        this.current = origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteWrapper byteWrapper = (ByteWrapper) o;

        return current == byteWrapper.current;
    }

    @Override
    public int hashCode() {
        return current;
    }

    @Override
    public String toString() {
        return String.valueOf(current);
    }

    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
