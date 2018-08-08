package quan.mongo;

/**
 * 集合包装器
 * Created by quanchangnai on 2018/8/6.
 */
public interface CollectionWrapper {

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
     * 修改数据时
     *
     * @param addedData
     */
    default void onUpdateData(Object addedData) {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (addedData instanceof ReferenceData) {
            MappingData addedDataOwner = ((ReferenceData) addedData).getOwner();
            if (addedDataOwner != null && addedDataOwner != getOwner()) {
                throw new UnsupportedOperationException("添加的数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + addedDataOwner);
            }
        }
        //交给事务管理
        transaction.addMappingData(getOwner());
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
