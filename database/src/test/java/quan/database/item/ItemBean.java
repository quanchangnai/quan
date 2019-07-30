package quan.database.item;

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;

/**
 * 道具<br/>
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

        String $name = object.getString("name");
        if ($name == null) {
            $name = "";
        }
        name.setValue($name);
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}