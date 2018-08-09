package quan.mongo;

/**
 * Integer包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class IntegerWrapper {

    //当前值
    private int current;

    //原始值
    private int origin;

    public IntegerWrapper() {
    }

    public IntegerWrapper(int value) {
        this.current = value;
        this.origin = value;
    }

    public int set(int value) {
        int ret = this.current;
        this.current = value;
        return ret;
    }

    public int get() {
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

    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
