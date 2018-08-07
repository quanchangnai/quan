package quan.mongo.test;

import quan.mongo.IntegerWrapper;

/**
 * 实际是生成的
 * Created by quanchangnai on 2018/8/7.
 */
public class ItemData extends BaseData {

    private IntegerWrapper itemId = new IntegerWrapper(0);

    private IntegerWrapper itemNum = new IntegerWrapper(0);

    public ItemData(int itemId, int itemNum) {
        this.itemId.set(itemId);
        this.itemNum.set(itemNum);
    }

    @Override
    public void commit() {
        itemId.commit();
        itemNum.commit();
    }

    @Override
    public void rollback() {
        itemId.rollback();
        itemNum.rollback();
    }

    public int getItemId() {
        return itemId.get();
    }

    public void setItemId(int itemId) {
        onUpdateData();
        this.itemId.set(itemId);
    }

    public int getItemNum() {
        return itemNum.get();
    }

    public void setItemNum(int itemNum) {
        onUpdateData();
        this.itemNum.set(itemNum);
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
                '}';
    }
}
