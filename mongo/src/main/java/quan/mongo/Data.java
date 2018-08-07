package quan.mongo;

/**
 * 支持提交、回滚的数据，用以实现事务
 * Created by quanchangnai on 2017/5/18.
 */
public interface Data {

    /**
     * 提交数据
     */
    default void commit() {
    }

    /**
     * 回滚数据
     */
    default void rollback() {
    }

    /**
     * 调试数据
     *
     * @return
     */
    default String toDebugString() {
        return this.toString();
    }
}
