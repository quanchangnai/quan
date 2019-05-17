package quan.transaction;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data {

    public abstract MappingData getRoot();


    /**
     * 更新数据回调
     *
     * @param refData
     */
    protected void onWriteData(ReferenceData refData) {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (refData != null) {
            MappingData refDataOwner = refData.getRoot();
            if (refDataOwner != null && refDataOwner != getRoot()) {
                throw new UnsupportedOperationException("设置的引用数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + refDataOwner);
            }
        }
        //交给事务管理
        transaction.addDataLog(getRoot());
    }
}
