package quan.database.item;

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;

/**
 * 道具<br/>
 * 自动生成
 */
public class ItemEntity extends Entity {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");


    public int getId() {
        return id.getValue();
    }

    public ItemEntity setId(int id) {
        this.id.setLogValue(id, _getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public ItemEntity setName(String name) {
        this.name.setLogValue(name, _getRoot());
        return this;
    }


    @Override
    protected void _setChildrenLogRoot(Data root) {
    }

    @Override
    public JSONObject encode() {
        JSONObject json = new JSONObject();

        json.put("id", this.id.getValue());
        json.put("name", this.name.getValue());

        return json;
    }

    @Override
    public void decode(JSONObject json) {
        this.id.setValue(json.getIntValue("id"));
        this.name.setValue(json.getOrDefault("name", "").toString());
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}