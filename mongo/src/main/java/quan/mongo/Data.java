package quan.mongo;

/**
 * 支持提交、回滚的数据，用以实现事务
 * Created by quanchangnai on 2017/5/18.
 */
public abstract class Data {

    /**
     * 拥有者
     *
     * @return
     */
    protected abstract MappingData getOwner();

    /**
     * 提交数据
     */
    protected abstract void commit();

    /**
     * 回滚数据
     */
    protected abstract void rollback();

    /**
     * 修改数据时
     *
     * @param setData
     */
    protected void onUpdateData(Object setData) {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (setData instanceof ReferenceData) {
            MappingData setDataOwner = ((ReferenceData) setData).getOwner();
            if (setDataOwner != null && setDataOwner != getOwner()) {
                throw new UnsupportedOperationException("设置的数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + setDataOwner);
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
    public String toDebugString() {
        return this.toString();
    }
}
