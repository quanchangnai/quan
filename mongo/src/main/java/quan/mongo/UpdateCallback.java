package quan.mongo;

/**
 * 修改数据回调
 * Created by quanchangnai on 2018/8/6.
 */
public interface UpdateCallback {

    void setMappingData(MappingData mappingData);

    MappingData getMappingData();

    default void onUpdateData() {
        Transaction transaction = Transaction.current();
        if (transaction == null) {
            throw new RuntimeException("当前不在事务中，禁止修改数据");
        }
        transaction.addMappingData(getMappingData());
    }
}
