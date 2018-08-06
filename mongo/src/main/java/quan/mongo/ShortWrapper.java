package quan.mongo;

/**
 * Short
 * Created by quanchangnai on 2017/5/23.
 */
public class ShortWrapper implements Data {

    //当前值
    private short current;

    //原始值
    private short origin;

    public ShortWrapper(short value) {
        this.current = value;
        this.origin = value;
    }

    public void set(short value) {
        this.current = value;
    }

    public short get() {
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

    @Override
    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
