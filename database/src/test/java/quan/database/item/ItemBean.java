package quan.database.item;

import com.alibaba.fastjson.JSONObject;
import quan.database.Bean;
import quan.database.Data;
import quan.database.field.BaseField;

/**
 * 道具
 * Created by 自动生成
 */
public class ItemBean extends Bean {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");

    public int getId() {
        return id.getValue();
    }

    public void setId(int id) {
        this.id.setLogValue(id, getRoot());
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setLogValue(name, getRoot());
    }

    @Override
    public void setChildrenLogRoot(Data root) {

    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("name", getName());
        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getIntValue("id"));
        name.setValue(object.getString("name"));
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}
