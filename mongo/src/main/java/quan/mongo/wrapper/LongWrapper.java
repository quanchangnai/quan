package quan.mongo.wrapper;

/**
 * long
 * Created by quanchangnai on 2017/5/23.
 */
public class LongWrapper implements TypeWrapper {
    //当前值
    private long current;
    //原始值
    private long origin;

    public LongWrapper(long value) {
        this.origin = value;
        this.current = value;
    }

    public void set(long value) {
        this.current = value;
    }

    public long get() {
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
