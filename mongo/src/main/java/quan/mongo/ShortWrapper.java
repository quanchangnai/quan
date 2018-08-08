package quan.mongo;

/**
 * Short包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class ShortWrapper {

    //当前值
    private short current;

    //原始值
    private short origin;

    public ShortWrapper(short value) {
        this.current = value;
        this.origin = value;
    }

    public short set(short value) {
        short ret = this.current;
        this.current = value;
        return ret;
    }

    public short get() {
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

        ShortWrapper transInt = (ShortWrapper) o;

        return current == transInt.current;
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
