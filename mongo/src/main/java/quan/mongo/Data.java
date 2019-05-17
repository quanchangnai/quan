package quan.mongo;

import org.bson.Document;

/**
 * 支持提交、回滚的数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class Data {

    /**
     * 数据的拥有者
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
     * 写数据回调
     *
     * @param refData 设置的引用数据，不是引用数据传null
     */
    protected void onWriteData(ReferenceData refData) {
        Transaction transaction = Transaction.current();
        if (transaction == null && !getOwner().isDecodingState()) {
            throw new UnsupportedOperationException("当前不在事务中，禁止修改数据");
        }
        if (refData != null) {
            MappingData refDataOwner = refData.getOwner();
            if (refDataOwner != null && refDataOwner != getOwner()) {
                throw new UnsupportedOperationException("设置的引用数据当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + refDataOwner);
            }
        }
        //交给事务管理
        transaction.addWriteData(getOwner());
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
