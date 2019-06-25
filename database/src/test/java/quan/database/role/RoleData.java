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

    public RoleData() {
    }

    @Override
    public Cache<Long, RoleData> getCache() {
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
        ItemBean itemBeanValue = this.itemBean.getValue();
        if (itemBeanValue != null) {
            itemBeanValue.setLogRoot(root);
        }
        this.set.setLogRoot(root);
        this.list.setLogRoot(root);
        this.map.setLogRoot(root);
        this.items.setLogRoot(root);
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

        object.put("id", getId());
        object.put("name", getName());

        ItemBean itemBean = this.itemBean.getValue();
        if (itemBean != null) {
            object.put("itemBean", itemBean.encode());
        }

        JSONArray set = new JSONArray();
        for (Boolean setValue : this.set) {
            set.add(setValue);
        }
        object.put("set", set);

        JSONArray list = new JSONArray();
        for (String listValue : this.list) {
            list.add(listValue);
        }
        object.put("list", list);

        JSONObject map = new JSONObject();
        for (Integer mapKey : this.map.keySet()) {
            map.put(String.valueOf(mapKey), this.map.get(mapKey));
        }
        object.put("map", map);

        JSONObject items = new JSONObject();
        for (Integer itemsKey : this.items.keySet()) {
            items.put(String.valueOf(itemsKey), this.items.get(itemsKey).encode());
        }
        object.put("items", items);

        return object;
    }

    @Override
    public void decode(JSONObject object) {

        this.id.setValue(object.getLongValue("id"));
        this.name.setValue(object.getString("name"));

        JSONObject itemBean = object.getJSONObject("itemBean");
        if (itemBean != null) {
            if (this.itemBean.getValue() == null) {
                this.itemBean.setValue(new ItemBean());
            }
            this.itemBean.getValue().decode(itemBean);
        }

        JSONArray set = object.getJSONArray("set");
        if (set != null) {
            Set<Boolean> set1 = new HashSet<>();
            for (int i = 0; i < set.size(); i++) {
                set1.add(set.getBoolean(i));
            }
            PSet<Boolean> set2 = Empty.set();
            this.set.setValue(set2.plusAll(set1));
        }

        JSONArray list = object.getJSONArray("list");
        if (list != null) {
            List<String> list1 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                list1.add(list.getString(i));
            }
            PVector<String> list2 = Empty.vector();
            this.list.setValue(list2.plusAll(list1));
        }

        JSONObject map = object.getJSONObject("map");
        if (map != null) {
            Map<Integer, Integer> map1 = new HashMap<>();
            for (String mapKey : map.keySet()) {
                map1.put(Integer.valueOf(mapKey), map.getInteger(mapKey));
            }
            PMap<Integer, Integer> map2 = Empty.map();
            this.map.setValue(map2.plusAll(map1));
        }

        JSONObject items = object.getJSONObject("items");
        if (items != null) {
            Map<Integer, ItemBean> items1 = new HashMap<>();
            for (String itemsKey : items.keySet()) {
                ItemBean items1Value = new ItemBean();
                items1Value.decode(items.getJSONObject(itemsKey));
                items1.put(Integer.valueOf(itemsKey), items1Value);
            }
            PMap<Integer, ItemBean> items2 = Empty.map();
            this.items.setValue(items2.plusAll(items1));
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
