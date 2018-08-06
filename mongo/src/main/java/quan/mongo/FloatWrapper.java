package quan.mongo;

import quan.mongo.Data;

/**
 * Float
 * Created by quanchangnai on 2017/5/23.
 */
public class FloatWrapper implements Data {

    //当前值
    private float current;

    //原始值
    private float origin;

    public FloatWrapper(float value) {
        this.origin = value;
        this.current = value;
    }

    public void set(float value) {
        this.current = value;
    }

    public float get() {
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
