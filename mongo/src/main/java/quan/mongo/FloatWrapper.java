package quan.mongo;

/**
 * Float包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class FloatWrapper {

    //当前值
    private float current;

    //原始值
    private float origin;

    public FloatWrapper(float value) {
        this.origin = value;
        this.current = value;
    }

    public float set(float value) {
        float ret = this.current;
        this.current = value;
        return ret;
    }

    public float get() {
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
