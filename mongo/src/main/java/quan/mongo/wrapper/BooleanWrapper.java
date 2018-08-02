package quan.mongo.wrapper;

/**
 * boolean
 * Created by quanchangnai on 2017/5/23.
 */
public class BooleanWrapper implements TypeWrapper {
    //当前值
    private boolean current;
    //原始值
    private boolean origin;

    public BooleanWrapper(boolean value) {
        this.current = value;
        this.origin = value;
    }

    public void set(boolean value) {
        this.current = value;
    }

    public boolean get() {
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

        BooleanWrapper booleanWrapper = (BooleanWrapper) o;

        return current == booleanWrapper.current;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(current);
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
