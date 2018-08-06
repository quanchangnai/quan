package quan.mongo;

/**
 * 数据，支持提交、回滚，用以实现事务
 * Created by quanchangnai on 2017/5/18.
 */
public interface Data {

    default void commit() {
    }

    default void rollback() {
    }

    default String toDebugString() {
        return this.toString();
    }
}
