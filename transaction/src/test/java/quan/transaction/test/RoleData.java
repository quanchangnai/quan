package quan.transaction.test;

import quan.transaction.MappingData;
import quan.transaction.field.BeanField;
import quan.transaction.field.IntField;
import quan.transaction.field.MapField;
import quan.transaction.field.StringField;

import java.util.Map;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class RoleData extends MappingData {

    private IntField id = new IntField();

    private StringField name = new StringField();

    private BeanField<ItemData> itemData = new BeanField<>();

    private MapField<Integer, Integer> maps = new MapField<>();

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

    public Map<Integer, Integer> getMaps() {
        return maps;
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "version=" + getVersion() +
                ",id=" + id +
                ", name=" + name +
                ", itemData=" + itemData +
                ", maps=" + maps +
                '}';
    }
}
