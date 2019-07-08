package quan.database.role;

import quan.database.*;
import org.pcollections.*;
import java.util.*;
import quan.database.item.ItemBean;
import com.alibaba.fastjson.*;

/**
 * 角色
 * Created by 自动生成
 */
public class RoleData extends Data<Long> {

    private static Cache<Long, RoleData> cache;

    public RoleData(Long id) {
	    super(cache);
        this.id.setLogValue(id, getRoot());
    }

    @Override
    public Long getKey() {
        return getId();
    }

    public synchronized static void setCache(Cache<Long, RoleData> cache) {
        cache.checkWorkable();
        if (RoleData.cache != null && RoleData.cache.isWorkable()) {
            throw new IllegalStateException("数据已设置缓存");
        }
        RoleData.cache = cache;
    }

    private synchronized static void checkCache() {
        if (cache != null && cache.isWorkable()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (cache == null) {
            cache = new Cache<>("RoleData", RoleData::new);
            database.registerCache(cache);
        } else if (!cache.isWorkable()) {
            database.registerCache(cache);
        }
    }

    public static RoleData get(Long id) {
        checkCache();
        return cache.get(id);
    }

    public static void delete(Long id) {
        checkCache();
        cache.delete(id);
    }

    public static void insert(RoleData data) {
        checkCache();
        cache.insert(data);
    }

    public static RoleData getOrInsert(Long id) {
        checkCache();
        return cache.getOrInsert(id);
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

    public String getName() {
        return name.getValue();
    }

    public RoleData setName(String name) {
        this.name.setLogValue(name, getRoot());
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

    public ItemBean getItem() {
        return item.getValue();
    }

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
        ItemBean _item = this.item.getValue();
        if (_item != null) {
            _item.setLogRoot(root);
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

        ItemBean _item = item.getValue();
        if (_item != null) {
            object.put("item", _item.encode());
        }

        JSONObject _items = new JSONObject();
        for (Integer _items_Key : items.keySet()) {
            _items.put(String.valueOf(_items_Key), items.get(_items_Key).encode());
        }
        object.put("items", _items);

        JSONArray _set = new JSONArray();
        for (Boolean _set_Value : set) {
            _set.add(_set_Value);
        }
        object.put("set", _set);

        JSONArray _list = new JSONArray();
        for (String _list_Value : list) {
            _list.add(_list_Value);
        }
        object.put("list", _list);

        JSONObject _map = new JSONObject();
        for (Integer _map_Key : map.keySet()) {
            _map.put(String.valueOf(_map_Key), map.get(_map_Key));
        }
        object.put("map", _map);

        JSONArray _set2 = new JSONArray();
        for (ItemBean _set2_Value : set2) {
            _set2.add(_set2_Value.encode());
        }
        object.put("set2", _set2);

        JSONArray _list2 = new JSONArray();
        for (ItemBean _list2_Value : list2) {
            _list2.add(_list2_Value.encode());
        }
        object.put("list2", _list2);

        JSONObject _map2 = new JSONObject();
        for (Integer _map2_Key : map2.keySet()) {
            _map2.put(String.valueOf(_map2_Key), map2.get(_map2_Key).encode());
        }
        object.put("map2", _map2);

        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getLongValue("id"));

        String _name = object.getString("name");
        if (_name == null) {
            _name = "";
        }
        name.setValue(_name);

        bo.setValue(object.getBooleanValue("bo"));
        by.setValue(object.getByteValue("by"));
        s.setValue(object.getShortValue("s"));
        i.setValue(object.getIntValue("i"));
        f.setValue(object.getFloatValue("f"));
        d.setValue(object.getDoubleValue("d"));

        JSONObject _item = object.getJSONObject("item");
        if (_item != null) {
            ItemBean _item_Value = item.getValue();
            if (_item_Value == null) {
                _item_Value = new ItemBean();
                item.setValue(_item_Value);
            }
            _item_Value.decode(_item);
        }

        JSONObject _items_1 = object.getJSONObject("items");
        if (_items_1 != null) {
            Map<Integer, ItemBean> _items_2 = new HashMap<>();
            for (String _items_1_Key : _items_1.keySet()) {
                ItemBean _items_Value = new ItemBean();
                _items_Value.decode(_items_1.getJSONObject(_items_1_Key));
                _items_2.put(Integer.valueOf(_items_1_Key), _items_Value);
            }
            PMap<Integer, ItemBean> _items_3 = Empty.map();
            items.setValue(_items_3.plusAll(_items_2));
        }

        JSONArray _set_1 = object.getJSONArray("set");
        if (_set_1 != null) {
            Set<Boolean> _set_2 = new HashSet<>();
            for (int i = 0; i < _set_1.size(); i++) {
                _set_2.add(_set_1.getBoolean(i));
            }
            PSet<Boolean> _set_3 = Empty.set();
            set.setValue(_set_3.plusAll(_set_2));
        }

        JSONArray _list_1 = object.getJSONArray("list");
        if (_list_1 != null) {
            List<String> _list_2 = new ArrayList<>();
            for (int i = 0; i < _list_1.size(); i++) {
                _list_2.add(_list_1.getString(i));
            }
            PVector<String> _list_3 = Empty.vector();
            list.setValue(_list_3.plusAll(_list_2));
        }

        JSONObject _map_1 = object.getJSONObject("map");
        if (_map_1 != null) {
            Map<Integer, Integer> _map_2 = new HashMap<>();
            for (String _map_1_Key : _map_1.keySet()) {
                _map_2.put(Integer.valueOf(_map_1_Key), _map_1.getInteger(_map_1_Key));
            }
            PMap<Integer, Integer> _map_3 = Empty.map();
            map.setValue(_map_3.plusAll(_map_2));
        }

        JSONArray _set2_1 = object.getJSONArray("set2");
        if (_set2_1 != null) {
            Set<ItemBean> _set2_2 = new HashSet<>();
            for (int i = 0; i < _set2_1.size(); i++) {
                ItemBean _set2_Value = new ItemBean();
                _set2_Value.decode(_set2_1.getJSONObject(i));
                _set2_2.add(_set2_Value);
            }
            PSet<ItemBean> _set2_3 = Empty.set();
            set2.setValue(_set2_3.plusAll(_set2_2));
        }

        JSONArray _list2_1 = object.getJSONArray("list2");
        if (_list2_1 != null) {
            List<ItemBean> _list2_2 = new ArrayList<>();
            for (int i = 0; i < _list2_1.size(); i++) {
                ItemBean _list2_Value = new ItemBean();
                _list2_Value.decode(_list2_1.getJSONObject(i));
                _list2_2.add(_list2_Value);
            }
            PVector<ItemBean> _list2_3 = Empty.vector();
            list2.setValue(_list2_3.plusAll(_list2_2));
        }

        JSONObject _map2_1 = object.getJSONObject("map2");
        if (_map2_1 != null) {
            Map<Integer, ItemBean> _map2_2 = new HashMap<>();
            for (String _map2_1_Key : _map2_1.keySet()) {
                ItemBean _map2_Value = new ItemBean();
                _map2_Value.decode(_map2_1.getJSONObject(_map2_1_Key));
                _map2_2.put(Integer.valueOf(_map2_1_Key), _map2_Value);
            }
            PMap<Integer, ItemBean> _map2_3 = Empty.map();
            map2.setValue(_map2_3.plusAll(_map2_2));
        }

    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
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

}
