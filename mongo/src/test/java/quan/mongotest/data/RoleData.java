package quan.mongotest.data;

import org.bson.Document;
import quan.mongo.ListWrapper;
import quan.mongo.LongWrapper;
import quan.mongo.MappingData;
import quan.mongo.ReferenceWrapper;

import java.util.List;

/**
 * 手写测试代码，实际应该是生成的
 * Created by quanchangnai on 2018/8/6.
 */
public class RoleData extends MappingData {

    private LongWrapper roleId = new LongWrapper(0);

    private ListWrapper<ItemData> items = new ListWrapper<>(getOwner());

    private ReferenceWrapper<ItemData> item = new ReferenceWrapper<>();

    private ListWrapper<Integer> list2 = new ListWrapper<>(getOwner());

    public RoleData(long roleId) {
        this.roleId.set(roleId);
    }


    @Override
    protected void commit() {
        items.commit();
        item.commit();
        list2.commit();
    }

    @Override
    protected void rollback() {
        items.rollback();
        item.rollback();
        list2.rollback();
    }



    public Long getRoleId() {
        return roleId.get();
    }

    public List<ItemData> getItems() {
        return items;
    }

    public ItemData getItem() {
        return item.get();
    }


    public ItemData setItem(ItemData item) {
        checkSetData(item);
        return this.item.set(item);
    }

    public List<Integer> getList2() {
        return list2;
    }

    @Override
    public Document doEncode() {
        return null;
    }

    @Override
    public void doDecode(Document document) {

    }

    @Override
    public String toString() {
        return "RoleData{" +
                "items=" + items +
                ", item=" + item +
                ", list2=" + list2 +
                '}';
    }

    @Override
    public String toDebugString() {
        return "RoleData{" +
                "items=" + items.toDebugString() +
                ", item=" + item.toDebugString() +
                ", list2=" + list2.toDebugString() +
                '}';
    }
}
