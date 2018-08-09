package quan.mongotest.data;

import org.bson.Document;
import quan.mongo.IntegerWrapper;
import quan.mongo.MappingData;
import quan.mongo.ReferenceData;

/**
 * 手写测试代码，实际应该是生成的
 * Created by quanchangnai on 2018/8/7.
 */
public class ItemData extends ReferenceData {

    private IntegerWrapper itemId = new IntegerWrapper(0);

    private IntegerWrapper itemNum = new IntegerWrapper(0);

    public ItemData(int itemId, int itemNum) {
        this.itemId.set(itemId);
        this.itemNum.set(itemNum);
    }

    @Override
    protected void commit() {
        super.commit();
        itemId.commit();
        itemNum.commit();
    }

    @Override
    protected void rollback() {
        super.rollback();
        itemId.rollback();
        itemNum.rollback();
    }

    @Override
    protected void setOwner(MappingData owner) {
        super.setOwner(owner);
        //其他
    }

    public int getItemId() {
        return itemId.get();
    }

    public int setItemId(int itemId) {
        checkSetData(null);
        return this.itemId.set(itemId);
    }

    public int getItemNum() {
        return itemNum.get();
    }

    public int setItemNum(int itemNum) {
        checkSetData(null);
        return this.itemNum.set(itemNum);
    }

    @Override
    public void doDecode(Document document) {

    }

    @Override
    public Document doEncode() {
        return null;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "itemId=" + itemId +
                ", itemNum=" + itemNum +
                '}';
    }

    @Override
    public String toDebugString() {
        return "ItemData{" +
                "itemId=" + itemId.toDebugString() +
                ", itemNum=" + itemNum.toDebugString() +
                ", owner=" + getOwner() +
                '}';
    }
}
