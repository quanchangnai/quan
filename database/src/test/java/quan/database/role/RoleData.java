package quan.database.role;

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;
import quan.database.item.ItemBean;

/**
 * 角色<br/>
 * Created by 自动生成
 */
public class RoleData extends Data<Long> {

    //角色ID
    private BaseField<Long> id = new BaseField<>(0L);

    private BaseField<String> name = new BaseField<>("");

    //角色类型
    private BaseField<Integer> roleType = new BaseField<>(0);

    private BaseField<Boolean> bo = new BaseField<>(false);

    private BaseField<Byte> by = new BaseField<>((byte) 0);

    private BaseField<Short> s = new BaseField<>((short) 0);

    private BaseField<Integer> i = new BaseField<>(0);

    private BaseField<Float> f = new BaseField<>(0F);

    private BaseField<Double> d = new BaseField<>(0D);

    //道具
    private BeanField<ItemBean> item = new BeanField<>();

    private MapField<Integer, ItemBean> items = new MapField<>(getRoot());

    private SetField<Boolean> set = new SetField<>(getRoot());

    private ListField<String> list = new ListField<>(getRoot());

    private MapField<Integer, Integer> map = new MapField<>(getRoot());

    private SetField<ItemBean> set2 = new SetField<>(getRoot());

    private ListField<ItemBean> list2 = new ListField<>(getRoot());

    private MapField<Integer, ItemBean> map2 = new MapField<>(getRoot());


    private static Cache<Long, RoleData> _cache;

    public RoleData(Long id) {
	    super(_cache);
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

    public String getName() {
        return name.getValue();
    }

    public RoleData setName(String name) {
        this.name.setLogValue(name, getRoot());
        return this;
    }

    /**
     * 角色类型
     */
    public RoleType getRoleType() {
        return RoleType.valueOf(roleType.getValue());
    }

    /**
     * 角色类型
     */
    public RoleData setRoleType(RoleType roleType) {
        this.roleType.setLogValue(roleType.getValue(), getRoot());
	    return this;
    }

    public boolean getBo() {
        return bo.getValue();
    }

    public RoleData setBo(boolean bo) {
        this.bo.setLogValue(bo, getRoot());
        return this;
    }

    public byte getBy() {
        return by.getValue();
    }

    public RoleData setBy(byte by) {
        this.by.setLogValue(by, getRoot());
        return this;
    }

    public short getS() {
        return s.getValue();
    }

    public RoleData setS(short s) {
        this.s.setLogValue(s, getRoot());
        return this;
    }

    public int getI() {
        return i.getValue();
    }

    public RoleData setI(int i) {
        this.i.setLogValue(i, getRoot());
        return this;
    }

    public float getF() {
        return f.getValue();
    }

    public RoleData setF(float f) {
        this.f.setLogValue(f, getRoot());
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public RoleData setD(double d) {
        this.d.setLogValue(d, getRoot());
        return this;
    }

    /**
     * 道具
     */
    public ItemBean getItem() {
        return item.getValue();
    }

    /**
     * 道具
     */
    public RoleData setItem(ItemBean item) {
        this.item.setLogValue(item, getRoot());
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
        object.put("roleType", roleType.getValue());
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

        JSONObject $items = new JSONObject();
        for (Integer $items$Key : items.keySet()) {
            $items.put(String.valueOf($items$Key), items.get($items$Key).encode());
        }
        object.put("items", $items);

        JSONArray $set = new JSONArray();
        $set.addAll(set);
        object.put("set", $set);

        JSONArray $list = new JSONArray();
        $list.addAll(list);
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

        roleType.setValue(object.getIntValue("roleType"));
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

        JSONObject $items$1 = object.getJSONObject("items");
        if ($items$1 != null) {
            PMap<Integer, ItemBean> $items$2 = Empty.map();
            for (String $items$1_Key : $items$1.keySet()) {
                ItemBean $items$Value = new ItemBean();
                $items$Value.decode($items$1.getJSONObject($items$1_Key));
                $items$2 = $items$2.plus(Integer.valueOf($items$1_Key), $items$Value);
            }
            items.setValue($items$2);
        }

        JSONArray $set$1 = object.getJSONArray("set");
        if ($set$1 != null) {
            PSet<Boolean> $set$2 = Empty.set();
            for (int i = 0; i < $set$1.size(); i++) {
                $set$2 = $set$2.plus($set$1.getBoolean(i));
            }
            set.setValue($set$2);
        }

        JSONArray $list$1 = object.getJSONArray("list");
        if ($list$1 != null) {
            PVector<String> $list$2 = Empty.vector();
            for (int i = 0; i < $list$1.size(); i++) {
                $list$2 = $list$2.plus($list$1.getString(i));
            }
            list.setValue($list$2);
        }

        JSONObject $map$1 = object.getJSONObject("map");
        if ($map$1 != null) {
            PMap<Integer, Integer> $map$2 = Empty.map();
            for (String $map$1_Key : $map$1.keySet()) {
                $map$2 = $map$2.plus(Integer.valueOf($map$1_Key), $map$1.getInteger($map$1_Key));
            }
            map.setValue($map$2);
        }

        JSONArray $set2$1 = object.getJSONArray("set2");
        if ($set2$1 != null) {
            PSet<ItemBean> $set2$2 = Empty.set();
            for (int i = 0; i < $set2$1.size(); i++) {
                ItemBean $set2$Value = new ItemBean();
                $set2$Value.decode($set2$1.getJSONObject(i));
                $set2$2 = $set2$2.plus($set2$Value);
            }
            set2.setValue($set2$2);
        }

        JSONArray $list2$1 = object.getJSONArray("list2");
        if ($list2$1 != null) {
            PVector<ItemBean> $list2$2 = Empty.vector();
            for (int i = 0; i < $list2$1.size(); i++) {
                ItemBean $list2$Value = new ItemBean();
                $list2$Value.decode($list2$1.getJSONObject(i));
                $list2$2 = $list2$2.plus($list2$Value);
            }
            list2.setValue($list2$2);
        }

        JSONObject $map2$1 = object.getJSONObject("map2");
        if ($map2$1 != null) {
            PMap<Integer, ItemBean> $map2$2 = Empty.map();
            for (String $map2$1_Key : $map2$1.keySet()) {
                ItemBean $map2$Value = new ItemBean();
                $map2$Value.decode($map2$1.getJSONObject($map2$1_Key));
                $map2$2 = $map2$2.plus(Integer.valueOf($map2$1_Key), $map2$Value);
            }
            map2.setValue($map2$2);
        }
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",roleType=" + RoleType.valueOf(roleType.getValue()) +
                ",bo=" + bo +
                ",by=" + by +
                ",s=" + s +
                ",i=" + i +
                ",f=" + f +
                ",d=" + d +
                ",item=" + item +
                ",items=" + items +
                ",set=" + set +
                ",list=" + list +
                ",map=" + map +
                ",set2=" + set2 +
                ",list2=" + list2 +
                ",map2=" + map2 +
                '}';

    }

    public synchronized static void setCache(Cache<Long, RoleData> cache) {
        cache.checkWorkable();
        if (_cache != null && _cache.isWorkable()) {
            throw new IllegalStateException("数据已设置缓存");
        }
        _cache = cache;
    }

    private synchronized static void checkCache() {
        if (_cache != null && _cache.isWorkable()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (_cache == null) {
            _cache = new Cache<>("RoleData", RoleData::new);
            database.registerCache(_cache);
        } else if (!_cache.isWorkable()) {
            database.registerCache(_cache);
        }
    }

    public static RoleData get(Long id) {
        checkCache();
        return _cache.get(id);
    }

    public static void delete(Long id) {
        checkCache();
        _cache.delete(id);
    }

    public static void insert(RoleData data) {
        checkCache();
        _cache.insert(data);
    }

    public static RoleData getOrInsert(Long id) {
        checkCache();
        return _cache.getOrInsert(id);
    }

}