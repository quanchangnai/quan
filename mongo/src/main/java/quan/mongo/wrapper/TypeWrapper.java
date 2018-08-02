package quan.mongo.wrapper;

/**
 * 类型包装器
 * Created by quanchangnai on 2017/5/18.
 */
public interface TypeWrapper {

    default void commit() {
    }

    default void rollback() {
    }

    default String toDebugString() {
        return this.toString();
    }
}
