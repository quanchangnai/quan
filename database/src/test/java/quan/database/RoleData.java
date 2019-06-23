package quan.database;

import quan.database.field.BaseField;
import quan.database.field.BeanField;
import quan.database.field.ListField;
import quan.database.field.MapField;

import java.util.Map;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class RoleData extends Data<Integer> {

    @Override
    public Integer primaryKey() {
        return getId();
    }

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");

    private BeanField<ItemBean> itemBean = new BeanField<>();

    private MapField<Integer, Integer> items = new MapField<>(getRoot());
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

    public ItemBean getItemBean() {
        return itemBean.getValue();
    }

    public RoleData setItemBean(ItemBean itemBean) {
        this.itemBean.setValue(itemBean);
        return this;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ", name=" + name +
                ", itemBean=" + itemBean +
                ", items=" + items +
                '}';
    }
}
