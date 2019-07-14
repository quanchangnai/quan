package quan.database.role;

import quan.database.*;
import org.pcollections.*;
import java.util.*;
import quan.database.item.ItemBean;
import com.alibaba.fastjson.*;
import quan.database.item.ItemType;

/**
 * 角色2<br/>
 * Created by 自动生成
 */
public class RoleData2 extends Data<Long> {

    public RoleData2() {
        super(null);
    }
        
    public RoleData2(Long id) {
        super(null);
        this.id.setLogValue(id, getRoot());
    }

    @Override
    public Long getKey() {
        return getId();
    }


    private BaseField<Long> id = new BaseField<>(0L);//角色ID

    private BaseField<String> name = new BaseField<>("");

    private BaseField<Boolean> bo = new BaseField<>(false);

    private BaseField<Byte> by = new BaseField<>((byte) 0);

    private BaseField<Short> s = new BaseField<>((short) 0);

    private BaseField<Integer> i = new BaseField<>(0);

    private BaseField<Float> f = new BaseField<>(0F);

    private BaseField<Double> d = new BaseField<>(0D);

    private BeanField<ItemBean> item = new BeanField<>();

    private BaseField<Integer> itemType = new BaseField<>(0);

    private MapField<Integer, ItemBean> items = new MapField<>(getRoot());

    private SetField<Boolean> set = new SetField<>(getRoot());

    private ListField<String> list = new ListField<>(getRoot());

    private MapField<Integer, Integer> map = new MapField<>(getRoot());

    private SetField<ItemBean> set2 = new SetField<>(getRoot());

    private ListField<ItemBean> list2 = new ListField<>(getRoot());

    private MapField<Integer, ItemBean> map2 = new MapField<>(getRoot());


    public long getId() {
        return id.getValue();
    }

    public RoleData2 setId(long id) {
        this.id.setLogValue(id, getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public RoleData2 setName(String name) {
        this.name.setLogValue(name, getRoot());
        return this;
    }

    public boolean getBo() {
        return bo.getValue();
    }

    public RoleData2 setBo(boolean bo) {
        this.bo.setLogValue(bo, getRoot());
        return this;
    }

    public byte getBy() {
        return by.getValue();
    }

    public RoleData2 setBy(byte by) {
        this.by.setLogValue(by, getRoot());
        return this;
    }

    public short getS() {
        return s.getValue();
    }

    public RoleData2 setS(short s) {
        this.s.setLogValue(s, getRoot());
        return this;
    }

    public int getI() {
        return i.getValue();
    }

    public RoleData2 setI(int i) {
        this.i.setLogValue(i, getRoot());
        return this;
    }

    public float getF() {
        return f.getValue();
    }

    public RoleData2 setF(float f) {
        this.f.setLogValue(f, getRoot());
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public RoleData2 setD(double d) {
        this.d.setLogValue(d, getRoot());
        return this;
    }

    public ItemBean getItem() {
        return item.getValue();
    }

    public RoleData2 setItem(ItemBean item) {
        this.item.setLogValue(item, getRoot());
        return this;
    }

    public ItemType getItemType() {
        return ItemType.valueOf(itemType.getValue());
    }

    public RoleData2 setItemType(ItemType itemType) {
        this.itemType.setLogValue(itemType.getValue(), getRoot());
	    return this;
    }

    public Map<Integer, ItemBean> getItems() {
        return items;
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

    public Set<ItemBean> getSet2() {
        return set2;
    }

    public List<ItemBean> getList2() {
        return list2;
    }

    public Map<Integer, ItemBean> getMap2() {
        return map2;
    }


    @Override
    public void setChildrenLogRoot(Data root) {
        ItemBean $item = this.item.getValue();
        if ($item != null) {
            $item.setLogRoot(root);
        }

        items.setLogRoot(root);
        set.setLogRoot(root);
        list.setLogRoot(root);
        map.setLogRoot(root);
        set2.setLogRoot(root);
        list2.setLogRoot(root);
        map2.setLogRoot(root);
    }

    @Override
    public JSONObject encode() {
        JSONObject object = new JSONObject();

        object.put("id", id.getValue());
        object.put("name", name.getValue());
        object.put("bo", bo.getValue());
        object.put("by", by.getValue());
        object.put("s", s.getValue());
        object.put("i", i.getValue());
        object.put("f", f.getValue());
        object.put("d", d.getValue());

        ItemBean $item = item.getValue();
        if ($item != null) {
            object.put("item", $item.encode());
        }

        object.put("itemType", itemType.getValue());
        JSONObject $items = new JSONObject();
        for (Integer $items$Key : items.keySet()) {
            $items.put(String.valueOf($items$Key), items.get($items$Key).encode());
        }
        object.put("items", $items);

        JSONArray $set = new JSONArray();
        for (Boolean $set$Value : set) {
            $set.add($set$Value);
        }
        object.put("set", $set);

        JSONArray $list = new JSONArray();
        for (String $list$Value : list) {
            $list.add($list$Value);
        }
        object.put("list", $list);

        JSONObject $map = new JSONObject();
        for (Integer $map$Key : map.keySet()) {
            $map.put(String.valueOf($map$Key), map.get($map$Key));
        }
        object.put("map", $map);

        JSONArray $set2 = new JSONArray();
        for (ItemBean $set2$Value : set2) {
            $set2.add($set2$Value.encode());
        }
        object.put("set2", $set2);

        JSONArray $list2 = new JSONArray();
        for (ItemBean $list2$Value : list2) {
            $list2.add($list2$Value.encode());
        }
        object.put("list2", $list2);

        JSONObject $map2 = new JSONObject();
        for (Integer $map2$Key : map2.keySet()) {
            $map2.put(String.valueOf($map2$Key), map2.get($map2$Key).encode());
        }
        object.put("map2", $map2);

        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getLongValue("id"));

        String $name = object.getString("name");
        if ($name == null) {
            $name = "";
        }
        name.setValue($name);

        bo.setValue(object.getBooleanValue("bo"));
        by.setValue(object.getByteValue("by"));
        s.setValue(object.getShortValue("s"));
        i.setValue(object.getIntValue("i"));
        f.setValue(object.getFloatValue("f"));
        d.setValue(object.getDoubleValue("d"));

        JSONObject $item = object.getJSONObject("item");
        if ($item != null) {
            ItemBean $item$Value = item.getValue();
            if ($item$Value == null) {
                $item$Value = new ItemBean();
                item.setValue($item$Value);
            }
            $item$Value.decode($item);
        }

        itemType.setValue(object.getIntValue("itemType"));
        JSONObject $items$1 = object.getJSONObject("items");
        if ($items$1 != null) {
            Map<Integer, ItemBean> $items$2 = new HashMap<>();
            for (String $items$1_Key : $items$1.keySet()) {
                ItemBean $items$Value = new ItemBean();
                $items$Value.decode($items$1.getJSONObject($items$1_Key));
                $items$2.put(Integer.valueOf($items$1_Key), $items$Value);
            }
            PMap<Integer, ItemBean> $items$3 = Empty.map();
            items.setValue($items$3.plusAll($items$2));
        }

        JSONArray $set$1 = object.getJSONArray("set");
        if ($set$1 != null) {
            Set<Boolean> $set$2 = new HashSet<>();
            for (int i = 0; i < $set$1.size(); i++) {
                $set$2.add($set$1.getBoolean(i));
            }
            PSet<Boolean> $set$3 = Empty.set();
            set.setValue($set$3.plusAll($set$2));
        }

        JSONArray $list$1 = object.getJSONArray("list");
        if ($list$1 != null) {
            List<String> $list$2 = new ArrayList<>();
            for (int i = 0; i < $list$1.size(); i++) {
                $list$2.add($list$1.getString(i));
            }
            PVector<String> $list$3 = Empty.vector();
            list.setValue($list$3.plusAll($list$2));
        }

        JSONObject $map$1 = object.getJSONObject("map");
        if ($map$1 != null) {
            Map<Integer, Integer> $map$2 = new HashMap<>();
            for (String $map$1_Key : $map$1.keySet()) {
                $map$2.put(Integer.valueOf($map$1_Key), $map$1.getInteger($map$1_Key));
            }
            PMap<Integer, Integer> $map$3 = Empty.map();
            map.setValue($map$3.plusAll($map$2));
        }

        JSONArray $set2$1 = object.getJSONArray("set2");
        if ($set2$1 != null) {
            Set<ItemBean> $set2$2 = new HashSet<>();
            for (int i = 0; i < $set2$1.size(); i++) {
                ItemBean $set2$Value = new ItemBean();
                $set2$Value.decode($set2$1.getJSONObject(i));
                $set2$2.add($set2$Value);
            }
            PSet<ItemBean> $set2$3 = Empty.set();
            set2.setValue($set2$3.plusAll($set2$2));
        }

        JSONArray $list2$1 = object.getJSONArray("list2");
        if ($list2$1 != null) {
            List<ItemBean> $list2$2 = new ArrayList<>();
            for (int i = 0; i < $list2$1.size(); i++) {
                ItemBean $list2$Value = new ItemBean();
                $list2$Value.decode($list2$1.getJSONObject(i));
                $list2$2.add($list2$Value);
            }
            PVector<ItemBean> $list2$3 = Empty.vector();
            list2.setValue($list2$3.plusAll($list2$2));
        }

        JSONObject $map2$1 = object.getJSONObject("map2");
        if ($map2$1 != null) {
            Map<Integer, ItemBean> $map2$2 = new HashMap<>();
            for (String $map2$1_Key : $map2$1.keySet()) {
                ItemBean $map2$Value = new ItemBean();
                $map2$Value.decode($map2$1.getJSONObject($map2$1_Key));
                $map2$2.put(Integer.valueOf($map2$1_Key), $map2$Value);
            }
            PMap<Integer, ItemBean> $map2$3 = Empty.map();
            map2.setValue($map2$3.plusAll($map2$2));
        }

    }

    @Override
    public String toString() {
        return "RoleData2{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",bo=" + bo +
                ",by=" + by +
                ",s=" + s +
                ",i=" + i +
                ",f=" + f +
                ",d=" + d +
                ",item=" + item +
                ",itemType=" + ItemType.valueOf(itemType.getValue()) +
                ",items=" + items +
                ",set=" + set +
                ",list=" + list +
                ",map=" + map +
                ",set2=" + set2 +
                ",list2=" + list2 +
                ",map2=" + map2 +
                '}';

    }

}
