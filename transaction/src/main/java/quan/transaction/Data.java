package quan.transaction;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data {

    public abstract MappingData getRoot();


    /**
     * 更新数据回调
     *
     * @param beanData
     */
    protected void onWriteData(BeanData beanData) {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (beanData != null) {
            MappingData root = beanData.getRoot();
            if (root != null && root != getRoot()) {
                throw new UnsupportedOperationException("设置的BeanData当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + root);
            }
        }
        //交给事务管理
        if (getRoot() != null) {
            transaction.addVersionLog(getRoot());
        }
    }
}
