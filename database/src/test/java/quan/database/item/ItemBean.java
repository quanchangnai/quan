package quan.database.item;

import quan.database.*;
import com.alibaba.fastjson.JSONArray;

import java.util.*;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.Empty;
import quan.database.field.*;
import com.alibaba.fastjson.JSONObject;
import org.pcollections.PMap;

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

        object.put("id", id.getValue());
        object.put("name", name.getValue());
        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getInteger("id"));
        name.setValue(object.getString("name"));
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

    public static void main(String[] args) {
        Transaction.execute(ItemBean::test);
    }

    private static boolean test(){
        JSONObject object = new JSONObject();

        Set<ItemBean> set = new HashSet<>();
        ItemBean itemBean = new ItemBean();
        itemBean.setId(111);
        itemBean.setName("111");
        set.add(itemBean);

        JSONArray _set = new JSONArray();
        for (ItemBean _set_value : set) {
            _set.add(_set_value);
        }
        object.put("set", _set);


        System.err.println(object);

        return true;
    }
}
