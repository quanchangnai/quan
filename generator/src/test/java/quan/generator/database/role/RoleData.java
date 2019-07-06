package quan.generator.database.role;

import quan.database.*;
import quan.database.Database;
import quan.database.Cache;
import com.alibaba.fastjson.JSONArray;
import quan.database.Data;
import quan.generator.database.item.ItemBean;
import java.util.*;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.Empty;
import com.alibaba.fastjson.JSONObject;
import org.pcollections.PMap;

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
        cache.checkClosed();
        if (RoleData.cache != null) {
            throw new IllegalStateException("数据已设置缓存");
        }
        RoleData.cache = cache;
    }


    private synchronized static void checkCache() {
        if (cache != null && !cache.isClosed()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库");
        }

        if (cache == null) {
            cache = new Cache<>("RoleData", RoleData::new);
            database.registerCache(cache);
        } else if (cache.isClosed()) {
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

    private BeanField<ItemBean> itemBean = new BeanField<>();

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

    public void setName(String name) {
        this.name.setLogValue(name, getRoot());
    }

    public boolean getBo() {
        return bo.getValue();
    }

    public void setBo(boolean bo) {
        this.bo.setLogValue(bo, getRoot());
    }

    public byte getBy() {
        return by.getValue();
    }

    public void setBy(byte by) {
        this.by.setLogValue(by, getRoot());
    }

    public short getS() {
        return s.getValue();
    }

    public void setS(short s) {
        this.s.setLogValue(s, getRoot());
    }

    public int getI() {
        return i.getValue();
    }

    public void setI(int i) {
        this.i.setLogValue(i, getRoot());
    }

    public float getF() {
        return f.getValue();
    }

    public void setF(float f) {
        this.f.setLogValue(f, getRoot());
    }

    public double getD() {
        return d.getValue();
    }

    public void setD(double d) {
        this.d.setLogValue(d, getRoot());
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
        ItemBean _itemBean = this.itemBean.getValue();
        if (_itemBean != null) {
            _itemBean.setLogRoot(root);
        }

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

        ItemBean _itemBean = itemBean.getValue();
        if (_itemBean != null) {
            object.put("itemBean", _itemBean.encode());
        }

        JSONArray _set = new JSONArray();
        for (Boolean _set_value : set) {
            _set.add(_set_value);
        }
        object.put("set", _set);

        JSONArray _list = new JSONArray();
        for (String _list_value : list) {
            _list.add(_list_value);
        }
        object.put("list", _list);

        JSONObject _map = new JSONObject();
        for (Integer _map_key : map.keySet()) {
            _map.put(String.valueOf(_map_key), map.get(_map_key));
        }
        object.put("map", _map);

        JSONArray _set2 = new JSONArray();
        for (ItemBean _set2_value : set2) {
            _set2.add(_set2_value.encode());
        }
        object.put("set2", _set2);

        JSONArray _list2 = new JSONArray();
        for (ItemBean _list2_value : list2) {
            _list2.add(_list2_value.encode());
        }
        object.put("list2", _list2);

        JSONObject _map2 = new JSONObject();
        for (Integer _map2_key : map2.keySet()) {
            _map2.put(String.valueOf(_map2_key), map2.get(_map2_key).encode());
        }
        object.put("map2", _map2);

        return object;
    }

    @Override
    public void decode(JSONObject object) {
        id.setValue(object.getLong("id"));
        name.setValue(object.getString("name"));
        bo.setValue(object.getBoolean("bo"));
        by.setValue(object.getByte("by"));
        s.setValue(object.getShort("s"));
        i.setValue(object.getInteger("i"));
        f.setValue(object.getFloat("f"));
        d.setValue(object.getDouble("d"));

        JSONObject _itemBean = object.getJSONObject("itemBean");
        ItemBean _itemBean_value = itemBean.getValue();
        if (_itemBean_value == null) {
            _itemBean_value = new ItemBean();
            itemBean.setValue(_itemBean_value);
        }
        _itemBean_value.decode(_itemBean);

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
            for (String _map_1_key : _map_1.keySet()) {
                _map_2.put(Integer.valueOf(_map_1_key), _map_1.getInteger(_map_1_key));
            }
            PMap<Integer, Integer> _map_3 = Empty.map();
            map.setValue(_map_3.plusAll(_map_2));
        }

        JSONArray _set2_1 = object.getJSONArray("set2");
        if (_set2_1 != null) {
            Set<ItemBean> _set2_2 = new HashSet<>();
            for (int i = 0; i < _set2_1.size(); i++) {
                ItemBean _set2_value = new ItemBean();
                _set2_value.decode(_set2_1.getJSONObject(i));
                _set2_2.add(_set2_value);
            }
            PSet<ItemBean> _set2_3 = Empty.set();
            set2.setValue(_set2_3.plusAll(_set2_2));
        }

        JSONArray _list2_1 = object.getJSONArray("list2");
        if (_list2_1 != null) {
            List<ItemBean> _list2_2 = new ArrayList<>();
            for (int i = 0; i < _list2_1.size(); i++) {
                ItemBean _list2_value = new ItemBean();
                _list2_value.decode(_list2_1.getJSONObject(i));
                _list2_2.add(_list2_value);
            }
            PVector<ItemBean> _list2_3 = Empty.vector();
            list2.setValue(_list2_3.plusAll(_list2_2));
        }

        JSONObject _map2_1 = object.getJSONObject("map2");
        if (_map2_1 != null) {
            Map<Integer, ItemBean> _map2_2 = new HashMap<>();
            for (String _map2_1_key : _map2_1.keySet()) {
                ItemBean _map2_value = new ItemBean();
                _map2_value.decode(_map2_1.getJSONObject(_map2_1_key));
                _map2_2.put(Integer.valueOf(_map2_1_key), _map2_value);
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
                ",itemBean=" + itemBean +
                ",set=" + set +
                ",list=" + list +
                ",map=" + map +
                ",set2=" + set2 +
                ",list2=" + list2 +
                ",map2=" + map2 +
                '}';

    }

}