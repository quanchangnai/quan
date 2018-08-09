package quan.mongo;

/**
 * String包装器
 * Created by quanchangnai on 2017/5/23.
 */
public class StringWrapper {

    //当前值
    private String current;

    //原始值
    private String origin;

    public StringWrapper() {
        this.origin = "";
        this.current = "";
    }

    public StringWrapper(String value) {
        this.origin = value;
        this.current = value;
    }

    public String set(String value) {
        String ret = this.current;
        this.current = value;
        return ret;
    }

    public String get() {
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
                "current='" + current + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }
}
