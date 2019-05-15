package quan.mongo;

/**
 * ReferenceData包装器
 * Created by quanchangnai on 2018/8/8.
 */
public class ReferenceWrapper<V extends ReferenceData> {

    //当前值
    private V current;

    //原始值
    private V origin;

    public void commit() {
        origin = current;
        if (current != null) {
            current.commit();
        }
    }

    public void rollback() {
        if (origin == current && current != null) {
            current.rollback();
        }
        if (origin != current && origin != null) {
            origin.rollback();
        }
        current = origin;
    }

    public V set(V value,MappingData owner) {
        if (value == null && current != null) {
            current.setOwner(null);
        }
        V ret = current;
        current = value;
        current.setOwner(owner);
        return ret;
    }

    public V get() {
        return current;
    }

    @Override
    public String toString() {
        return "" + current;
    }

    public String toDebugString() {
        String currentStr = "null";
        if (current != null) {
            currentStr = current.toDebugString();
        }
        String originStr = "null";
        if (current != null) {
            originStr = origin.toDebugString();
        }
        return "ReferenceWrapper{" +
                "current=" + currentStr +
                ", origin=" + originStr +
                '}';

    }
}
