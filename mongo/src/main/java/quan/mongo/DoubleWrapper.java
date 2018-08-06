package quan.mongo;

import quan.mongo.Data;

/**
 * Double
 * Created by quanchangnai on 2017/5/23.
 */
public class DoubleWrapper implements Data {

    //当前值
    private double current;

    //原始值
    private double origin;

    public DoubleWrapper(double value) {
        this.origin = value;
        this.current = value;
    }

    public void set(double value) {
        this.current = value;
    }

    public double get() {
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
