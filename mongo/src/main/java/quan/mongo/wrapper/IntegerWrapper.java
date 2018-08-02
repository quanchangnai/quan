package quan.mongo.wrapper;

/**
 * int
 * Created by quanchangnai on 2017/5/23.
 */
public class IntegerWrapper implements TypeWrapper {
    //当前值
    private int current;
    //原始值
    private int origin;

    public IntegerWrapper(int value) {
        this.current = value;
        this.origin = value;
    }

    public void set(int value) {
        this.current = value;
    }

    public int get() {
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

        IntegerWrapper integerWrapper = (IntegerWrapper) o;

        return current == integerWrapper.current;
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
