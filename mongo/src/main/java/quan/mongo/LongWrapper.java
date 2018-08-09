package quan.mongo;

/**
 * Long包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class LongWrapper {

    //当前值
    private long current;

    //原始值
    private long origin;

    public LongWrapper() {
    }

    public LongWrapper(long value) {
        this.origin = value;
        this.current = value;
    }

    public long set(long value) {
        long ret = this.current;
        this.current = value;
        return ret;
    }

    public long get() {
        return this.current;
    }

    public void commit() {
        this.origin = current;
    }

    public void rollback() {
        this.current = origin;
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
