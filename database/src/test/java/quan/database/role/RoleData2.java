package quan.database.role;

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;
import quan.database.item.ItemEntity;
import quan.database.item.ItemType;

/**
 * 角色2<br/>
 * Created by 自动生成
 */
public class RoleData2 extends Data<Long> {

    //角色ID
    private BaseField<Long> id = new BaseField<>(0L);

    private BaseField<String> name = new BaseField<>("");

    private BaseField<Boolean> bo = new BaseField<>(false);

    private BaseField<Byte> by = new BaseField<>((byte) 0);

    private BaseField<Short> s = new BaseField<>((short) 0);

    private BaseField<Integer> i = new BaseField<>(0);

    private BaseField<Float> f = new BaseField<>(0F);

    private BaseField<Double> d = new BaseField<>(0D);

    private EntityField<ItemEntity> item = new EntityField<>();

    private BaseField<Integer> itemType = new BaseField<>(0);

    private MapField<Integer, ItemEntity> items = new MapField<>(getRoot());

    private SetField<Boolean> set = new SetField<>(getRoot());

    private ListField<String> list = new ListField<>(getRoot());

    private MapField<Integer, Integer> map = new MapField<>(getRoot());

    private SetField<ItemEntity> set2 = new SetField<>(getRoot());

    private ListField<ItemEntity> list2 = new ListField<>(getRoot());

    private MapField<Integer, ItemEntity> map2 = new MapField<>(getRoot());


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

    /**
     * 角色ID
     */
    public long getId() {
        return id.getValue();
    }

    /**
     * 角色ID
     */
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

    public ItemEntity getItem() {
        return item.getValue();
    }

    public RoleData2 setItem(ItemEntity item) {
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

    public Map<Integer, ItemEntity> getItems() {
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

    public Set<ItemEntity> getSet2() {
        return set2;
    }

    public List<ItemEntity> getList2() {
        return list2;
    }

    public Map<Integer, ItemEntity> getMap2() {
        return map2;
    }


    @Override
    public void setChildrenLogRoot(Data root) {
        ItemEntity $item = this.item.getValue();
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
        JSONObject $json$ = new JSONObject();

        $json$.put("id", id.getValue());
        $json$.put("name", name.getValue());
        $json$.put("bo", bo.getValue());
        $json$.put("by", by.getValue());
        $json$.put("s", s.getValue());
        $json$.put("i", i.getValue());
        $json$.put("f", f.getValue());
        $json$.put("d", d.getValue());

        ItemEntity $item = item.getValue();
        if ($item != null) {
            $json$.put("item", $item.encode());
        }

        $json$.put("itemType", itemType.getValue());

        JSONObject $items = new JSONObject();
        for (Integer $items$Key : items.keySet()) {
            $items.put(String.valueOf($items$Key), items.get($items$Key).encode());
        }
        $json$.put("items", $items);

        JSONArray $set = new JSONArray();
        $set.addAll(set);
        $json$.put("set", $set);

        JSONArray $list = new JSONArray();
        $list.addAll(list);
        $json$.put("list", $list);

        JSONObject $map = new JSONObject();
        for (Integer $map$Key : map.keySet()) {
            $map.put(String.valueOf($map$Key), map.get($map$Key));
        }
        $json$.put("map", $map);

        JSONArray $set2 = new JSONArray();
        for (ItemEntity $set2$Value : set2) {
            $set2.add($set2$Value.encode());
        }
        $json$.put("set2", $set2);

        JSONArray $list2 = new JSONArray();
        for (ItemEntity $list2$Value : list2) {
            $list2.add($list2$Value.encode());
        }
        $json$.put("list2", $list2);

        JSONObject $map2 = new JSONObject();
        for (Integer $map2$Key : map2.keySet()) {
            $map2.put(String.valueOf($map2$Key), map2.get($map2$Key).encode());
        }
        $json$.put("map2", $map2);

        return $json$;
    }

    @Override
    public void decode(JSONObject $json$) {
        id.setValue($json$.getLongValue("id"));

        String $name = $json$.getString("name");
        if ($name == null) {
            $name = "";
        }
        name.setValue($name);

        bo.setValue($json$.getBooleanValue("bo"));
        by.setValue($json$.getByteValue("by"));
        s.setValue($json$.getShortValue("s"));
        i.setValue($json$.getIntValue("i"));
        f.setValue($json$.getFloatValue("f"));
        d.setValue($json$.getDoubleValue("d"));

        JSONObject $item = $json$.getJSONObject("item");
        if ($item != null) {
            ItemEntity $item$Value = item.getValue();
            if ($item$Value == null) {
                $item$Value = new ItemEntity();
                item.setValue($item$Value);
            }
            $item$Value.decode($item);
        }

        itemType.setValue($json$.getIntValue("itemType"));

        JSONObject $items$1 = $json$.getJSONObject("items");
        if ($items$1 != null) {
            PMap<Integer, ItemEntity> $items$2 = Empty.map();
            for (String $items$1_Key : $items$1.keySet()) {
                ItemEntity $items$Value = new ItemEntity();
                $items$Value.decode($items$1.getJSONObject($items$1_Key));
                $items$2 = $items$2.plus(Integer.valueOf($items$1_Key), $items$Value);
            }
            items.setValue($items$2);
        }

        JSONArray $set$1 = $json$.getJSONArray("set");
        if ($set$1 != null) {
            PSet<Boolean> $set$2 = Empty.set();
            for (int i = 0; i < $set$1.size(); i++) {
                $set$2 = $set$2.plus($set$1.getBoolean(i));
            }
            set.setValue($set$2);
        }

        JSONArray $list$1 = $json$.getJSONArray("list");
        if ($list$1 != null) {
            PVector<String> $list$2 = Empty.vector();
            for (int i = 0; i < $list$1.size(); i++) {
                $list$2 = $list$2.plus($list$1.getString(i));
            }
            list.setValue($list$2);
        }

        JSONObject $map$1 = $json$.getJSONObject("map");
        if ($map$1 != null) {
            PMap<Integer, Integer> $map$2 = Empty.map();
            for (String $map$1_Key : $map$1.keySet()) {
                $map$2 = $map$2.plus(Integer.valueOf($map$1_Key), $map$1.getInteger($map$1_Key));
            }
            map.setValue($map$2);
        }

        JSONArray $set2$1 = $json$.getJSONArray("set2");
        if ($set2$1 != null) {
            PSet<ItemEntity> $set2$2 = Empty.set();
            for (int i = 0; i < $set2$1.size(); i++) {
                ItemEntity $set2$Value = new ItemEntity();
                $set2$Value.decode($set2$1.getJSONObject(i));
                $set2$2 = $set2$2.plus($set2$Value);
            }
            set2.setValue($set2$2);
        }

        JSONArray $list2$1 = $json$.getJSONArray("list2");
        if ($list2$1 != null) {
            PVector<ItemEntity> $list2$2 = Empty.vector();
            for (int i = 0; i < $list2$1.size(); i++) {
                ItemEntity $list2$Value = new ItemEntity();
                $list2$Value.decode($list2$1.getJSONObject(i));
                $list2$2 = $list2$2.plus($list2$Value);
            }
            list2.setValue($list2$2);
        }

        JSONObject $map2$1 = $json$.getJSONObject("map2");
        if ($map2$1 != null) {
            PMap<Integer, ItemEntity> $map2$2 = Empty.map();
            for (String $map2$1_Key : $map2$1.keySet()) {
                ItemEntity $map2$Value = new ItemEntity();
                $map2$Value.decode($map2$1.getJSONObject($map2$1_Key));
                $map2$2 = $map2$2.plus(Integer.valueOf($map2$1_Key), $map2$Value);
            }
            map2.setValue($map2$2);
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