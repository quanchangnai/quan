package quan.database;

import quan.database.field.BaseField;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class ItemBean extends Bean {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");

    public int getId() {
        return id.getValue();
    }

    public ItemBean setId(int id) {
        this.id.setLogValue(id, getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public ItemBean setName(String name) {
        this.name.setLogValue(name, getRoot());
        return this;
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
