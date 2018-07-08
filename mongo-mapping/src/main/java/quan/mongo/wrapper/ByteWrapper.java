package quan.mongo.wrapper;

/**
 * byte
 * Created by quanchangnai on 2017/5/23.
 */
public class ByteWrapper implements TypeWrapper {
    //当前值
    private byte current;
    //原始值
    private byte origin;

    public ByteWrapper(byte value) {
        this.current = value;
        this.origin = value;
    }

    public void set(byte value) {
        this.current = value;
    }

    public byte get() {
        return this.current;
    }

    @Override
    public void commit() {
        this.origin = current;
    }

    @Override
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

    @Override
    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
