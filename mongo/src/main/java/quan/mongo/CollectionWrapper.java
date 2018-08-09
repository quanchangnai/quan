package quan.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 集合包装器，支持提交、回滚数据
 * Created by quanchangnai on 2018/8/6.
 */
public interface CollectionWrapper {

    /**
     * 允许添加到集合里的数据类型
     */
    List<Class<?>> allowedClasses = Arrays.asList(Byte.class, Boolean.class, Short.class, Integer.class, Long.class, Double.class, String.class);

    /**
     * 拥有者
     *
     * @param owner
     */
    void setOwner(MappingData owner);

    /**
     * 拥有者
     */
    MappingData getOwner();

    /**
     * 校验修改数据
     *
     * @param insert       true：插入操作，false：删除操作
     * @param insertedData 插入的数据
     */
    default void checkUpdateData(boolean insert, Object insertedData) {
        Transaction transaction = Transaction.current();
        if (transaction == null && !getOwner().isDecodingState()) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }

        if (insert) {
            Objects.requireNonNull(insertedData, "不允许添加空数据");
            Class<?> insertedClass = insertedData.getClass();
            if (!allowedClasses.contains(insertedClass) && insertedClass.isAssignableFrom(ReferenceData.class)) {
                throw new IllegalArgumentException("不允许添加该数据类型：" + insertedClass);
            }
            if (insertedData instanceof ReferenceData) {
                MappingData addedDataOwner = ((ReferenceData) insertedData).getOwner();
                if (addedDataOwner != null && addedDataOwner != getOwner()) {
                    throw new UnsupportedOperationException("添加的数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + addedDataOwner);
                }
            }
        }

        //交给事务管理
        transaction.manageData(getOwner());
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
