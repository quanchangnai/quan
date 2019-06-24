package quan.database.role;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;
import quan.database.Cache;
import quan.database.Data;
import quan.database.field.*;
import quan.database.item.ItemBean;

import java.util.*;

/**
 * 角色
 * Created by 自动生成
 */
public class RoleData extends Data<Long> {

    private BaseField<Long> id = new BaseField<>(0L);//角色ID

    private BaseField<String> name = new BaseField<>("");

    private BeanField<ItemBean> itemBean = new BeanField<>();

    private SetField<Boolean> set = new SetField<>(getRoot());

    private ListField<String> list = new ListField<>(getRoot());

    private MapField<Integer, Integer> map = new MapField<>(getRoot());

    private MapField<Integer, ItemBean> items = new MapField<>(getRoot());

    public static final Cache<Long, RoleData> cache = new Cache<>(RoleData.class.getSimpleName(), RoleData::new);

    private RoleData() {
    }

    @Override
    public Cache<Long, RoleData> cache() {
        return cache;
    }

    @Override
    public Long getKey() {
        return getId();
    }

    @Override
    public void setKey(Long key) {
        setId(key);
    }

    public long getId() {
        return id.getValue();
    }

    public void setId(long id) {
        this.id.setLogValue(id, getRoot());
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setLogValue(name, getRoot());
    }

    public ItemBean getItemBean() {
        return itemBean.getValue();
    }

    public void setItemBean(ItemBean itemBean) {
        this.itemBean.setLogValue(itemBean, getRoot());
    }

    public Set<Boolean> getSet() {
        return set;
    }

    public List<String> getList() {
        return list;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public Map<Integer, ItemBean> getItems() {
        return items;
    }

    @Override
    public void setChildrenLogRoot(Data root) {
        ItemBean itemBeanValue = itemBean.getValue();
        if (itemBeanValue != null) {
            itemBeanValue.setLogRoot(root);
        }
        set.setLogRoot(root);
        list.setLogRoot(root);
        map.setLogRoot(root);
        items.setLogRoot(root);
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

        object.put("id", getId());
        object.put("name", getName());

        if (itemBean.getValue() != null) {
            object.put("itemBean", itemBean.getValue().encode());
        }

        JSONArray setArray = new JSONArray();
        for (Boolean setValue : set) {
            setArray.add(setValue);
        }
        object.put("set", setArray);

        JSONArray listArray = new JSONArray();
        for (String listValue : list) {
            listArray.add(listValue);
        }
        object.put("list", listArray);

        JSONObject mapObject = new JSONObject();
        for (Integer mapKey : map.keySet()) {
            mapObject.put(String.valueOf(mapKey), map.get(mapKey));
        }
        object.put("map", mapObject);

        JSONObject itemsObject = new JSONObject();
        for (Integer itemsKey : items.keySet()) {
            itemsObject.put(String.valueOf(itemsKey), items.get(itemsKey).encode());
        }
        object.put("items", itemsObject);

        return object;
    }

    @Override
    public void decode(JSONObject object) {

        this.id.setValue(object.getLongValue("id"));
        this.name.setValue(object.getString("name"));

        JSONObject itemBeanObject = object.getJSONObject("itemBean");
        if (itemBeanObject != null) {
            if (this.itemBean.getValue() == null) {
                this.itemBean.setValue(new ItemBean());
            }
            this.itemBean.getValue().decode(itemBeanObject);
        }

        JSONArray setArray = object.getJSONArray("set");
        if (setArray != null) {
            Set<Boolean> set = new HashSet<>();
            for (int i = 0; i < setArray.size(); i++) {
                set.add(setArray.getBoolean(i));
            }
            PSet<Boolean> pSet = Empty.set();
            this.set.setValue(pSet.plusAll(set));
        }

        JSONArray listArray = object.getJSONArray("list");
        if (listArray != null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < listArray.size(); i++) {
                list.add(listArray.getString(i));
            }
            PVector<String> pVector = Empty.vector();
            this.list.setValue(pVector.plusAll(list));
        }

        JSONObject mapObject = object.getJSONObject("map");
        if (mapObject != null) {
            Map<Integer, Integer> map = new HashMap<>();
            for (String mapKey : mapObject.keySet()) {
                map.put(Integer.valueOf(mapKey), mapObject.getInteger(mapKey));
            }
            PMap<Integer, Integer> pMap = Empty.map();
            this.map.setValue(pMap.plusAll(map));
        }

        JSONObject itemsObject = object.getJSONObject("items");
        if (itemsObject != null) {
            Map<Integer, ItemBean> map = new HashMap<>();
            for (String itemsKey : itemsObject.keySet()) {
                ItemBean itemBean = new ItemBean();
                itemBean.decode(itemsObject.getJSONObject(itemsKey));
                map.put(Integer.valueOf(itemsKey), itemBean);
            }
            PMap<Integer, ItemBean> pMap = Empty.map();
            this.items.setValue(pMap.plusAll(map));
        }

    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",itemBean=" + itemBean +
                ",set=" + set +
                ",list=" + list +
                ",map=" + map +
                ",items=" + items +
                '}';

    }

}
