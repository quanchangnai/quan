package quan.transaction;

import quan.transaction.field.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class RoleData extends MappingData {

    private IntField id = new IntField();

    private StringField name = new StringField();

    private BeanField<ItemData> itemData = new BeanField<>();

    private MapField<Integer, ItemData> map = new MapField<>(getRoot());

    private SetField<Integer> set = new SetField<>(getRoot());

    private ListField<Integer> list = new ListField<>(getRoot());



    public int getId() {
        return id.getValue();
    }

    public RoleData setId(int id) {
        this.id.setLogValue(id, getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public RoleData setName(String name) {
        this.name.setLogValue(name, getRoot());
        return this;
    }

    public ItemData getItemData() {
        return itemData.getValue();
    }

    public RoleData setItemData(ItemData itemData) {
        this.itemData.setLogValue(itemData, getRoot());
        return this;
    }

    public Map<Integer, ItemData> getMap() {
        return map;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public List<Integer> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "version=" + getVersion() +
                ",id=" + id +
                ", name=" + name +
                ", itemData=" + itemData +
                ", map=" + map +
                ", set=" + set +
                ", list=" + list +
                '}';
    }
}
