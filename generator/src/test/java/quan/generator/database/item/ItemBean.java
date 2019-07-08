package quan.generator.database.item;

import quan.database.*;
import org.pcollections.*;
import java.util.*;
import com.alibaba.fastjson.*;

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
    public void setChildrenLogRoot(Data root) {
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

        object.put("id", id.getValue());
        object.put("name", name.getValue());
        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getIntValue("id"));

        String _name = object.getString("name");
        if (_name == null) {
            _name = "";
        }
        name.setValue(_name);

    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}
