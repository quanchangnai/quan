package quan.mongotest.data;

import quan.mongo.ListWrapper;
import quan.mongo.MappingData;
import quan.mongo.ReferenceWrapper;

import java.util.List;

/**
 * 手写测试代码，实际应该是生成的
 * Created by quanchangnai on 2018/8/6.
 */
public class RoleData extends MappingData {

    private ListWrapper<ItemData> items = new ListWrapper<>(getOwner());

    private ReferenceWrapper<ItemData> item = new ReferenceWrapper<>();

    public RoleData() {
    }

    @Override
    protected void commit() {
        items.commit();
        item.commit();
    }

    @Override
    protected void rollback() {
        items.rollback();
        item.rollback();
    }

    public List<ItemData> getItems() {
        return items;
    }

    public ItemData getItem() {
        return item.get();
    }


    public ItemData setItem(ItemData item) {
        onUpdateData(item);
        return this.item.set(item);
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "items=" + items +
                ", item=" + item +
                '}';
    }

    @Override
    public String toDebugString() {
        return "RoleData{" +
                "items=" + items.toDebugString() +
                ", item=" + item.toDebugString() +
                '}';
    }
}
