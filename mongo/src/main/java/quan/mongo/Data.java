package quan.mongo;

import org.bson.Document;

/**
 * 支持提交、回滚的数据，用以实现事务
 * Created by quanchangnai on 2018/8/6.
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
     * 校验设置数据
     *
     * @param setData 设置的引用数据
     */
    protected void checkSetData(ReferenceData setData) {
        Transaction transaction = Transaction.current();
        if (transaction == null && !getOwner().isDecodingState()) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (setData != null) {
            MappingData setDataOwner = setData.getOwner();
            if (setDataOwner != null && setDataOwner != getOwner()) {
                throw new UnsupportedOperationException("设置的数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + setDataOwner);
            }
        }
        //交给事务管理
        transaction.manageData(getOwner());
    }

    /**
     * 编码
     */
    public abstract Document doEncode();

    /**
     * 解码
     *
     * @param document
     */
    public abstract void doDecode(Document document);

    /**
     * 调试数据
     *
     * @return
     */
    public String toDebugString() {
        return this.toString();
    }
}
