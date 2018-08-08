package quan.mongo;

/**
 * Boolean包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class BooleanWrapper {

    //当前值
    private boolean current;

    //原始值
    private boolean origin;

    public BooleanWrapper(boolean value) {
        this.current = value;
        this.origin = value;
    }

    public boolean set(boolean value) {
        boolean ret =  this.current;
        this.current = value;
        return ret;
    }

    public boolean get() {
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

    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
