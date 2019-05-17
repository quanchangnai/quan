package quan.mongotest.data;

import org.bson.Document;
import quan.mongo.*;

import java.util.List;
import java.util.Set;

/**
 * 手写测试代码，实际应该是生成的
 * Created by quanchangnai on 2018/8/6.
 */
public class RoleData extends MappingData {

    private LongWrapper roleId = new LongWrapper(0);

    private ListContainer<ItemData> items = new ListContainer<>(getOwner());

    private ReferenceWrapper<ItemData> item = new ReferenceWrapper<>();

    private SetContainer<Integer> set = new SetContainer<>(getOwner());


    public RoleData(long roleId) {
        this.roleId.set(roleId);
    }


    @Override
    protected void commit() {
        items.commit();
        item.commit();
        set.commit();
    }

    @Override
    protected void rollback() {
        items.rollback();
        item.rollback();
        set.rollback();
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
        onWriteData(item);
        return this.item.set(item, getOwner());
    }

    public Set<Integer> getSet() {
        return set;
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
                ", set=" + set +
                '}';
    }

    @Override
    public String toDebugString() {
        return "RoleData{" +
                "items=" + items.toDebugString() +
                ", item=" + item.toDebugString() +
                ", set=" + set.toDebugString() +
                '}';
    }
}
