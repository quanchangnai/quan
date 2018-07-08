package quan.mongo.wrapper;

/**
 * String
 * Created by quanchangnai on 2017/5/23.
 */
public class StringWrapper implements TypeWrapper {
    //当前值
    private String current;
    //原始值
    private String origin;

    public StringWrapper(String value) {
        this.origin = value;
        this.current = value;
    }

    public void set(String value) {
        this.current = value;
    }

    public String get() {
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
                "current='" + current + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }
}
