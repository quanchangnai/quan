package quan.mongo;

/**
 * Double包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class DoubleWrapper {

    //当前值
    private double current;

    //原始值
    private double origin;

    public DoubleWrapper() {
    }

    public DoubleWrapper(double value) {
        this.origin = value;
        this.current = value;
    }

    public double set(double value) {
        double ret = this.current;
        this.current = value;
        return ret;
    }

    public double get() {
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
