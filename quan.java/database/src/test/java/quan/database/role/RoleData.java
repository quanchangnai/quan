package quan.database.role;

import java.util.*;
import com.alibaba.fastjson.*;
import org.pcollections.*;
import quan.database.*;
import quan.database.item.ItemEntity;

/**
 * 角色<br/>
 * 自动生成
 */
public class RoleData extends Data<Long> {

    //角色ID
    private BaseField<Long> id = new BaseField<>(0L);

    private BaseField<String> name = new BaseField<>("");

    //角色类型
    private BaseField<Integer> roleType = new BaseField<>(0);

    private BaseField<Boolean> b = new BaseField<>(false);

    private BaseField<Short> s = new BaseField<>((short) 0);

    private BaseField<Integer> i = new BaseField<>(0);

    private BaseField<Float> f = new BaseField<>(0F);

    private BaseField<Double> d = new BaseField<>(0D);

    //道具
    private EntityField<ItemEntity> item = new EntityField<>();

    private MapField<Integer, ItemEntity> items = new MapField<>(_getRoot());

    private SetField<Boolean> set = new SetField<>(_getRoot());

    private ListField<String> list = new ListField<>(_getRoot());

    private MapField<Integer, Integer> map = new MapField<>(_getRoot());

    private SetField<ItemEntity> set2 = new SetField<>(_getRoot());

    private ListField<ItemEntity> list2 = new ListField<>(_getRoot());

    private MapField<Integer, ItemEntity> map2 = new MapField<>(_getRoot());


    private static Table<Long, RoleData> _table;

    public RoleData(Long id) {
        super(_table);
        this.id.setLogValue(id, _getRoot());
    }

    public RoleData(Table<Long, RoleData> table, Long id) {
        super(table);
        this.id.setLogValue(id, _getRoot());
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
        this.name.setLogValue(name, _getRoot());
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
        this.roleType.setLogValue(roleType.getValue(), _getRoot());
        return this;
    }

    public boolean getB() {
        return b.getValue();
    }

    public RoleData setB(boolean b) {
        this.b.setLogValue(b, _getRoot());
        return this;
    }

    public short getS() {
        return s.getValue();
    }

    public RoleData setS(short s) {
        this.s.setLogValue(s, _getRoot());
        return this;
    }

    public int getI() {
        return i.getValue();
    }

    public RoleData setI(int i) {
        this.i.setLogValue(i, _getRoot());
        return this;
    }

    public float getF() {
        return f.getValue();
    }

    public RoleData setF(float f) {
        this.f.setLogValue(f, _getRoot());
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public RoleData setD(double d) {
        this.d.setLogValue(d, _getRoot());
        return this;
    }

    /**
     * 道具
     */
    public ItemEntity getItem() {
        return item.getValue();
    }

    /**
     * 道具
     */
    public RoleData setItem(ItemEntity item) {
        this.item.setLogValue(item, _getRoot());
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
    protected void _setChildrenLogRoot(Data root) {
        ItemEntity $item = this.item.getValue();
        if ($item != null) {
            _setLogRoot($item, root);
        }

        _setLogRoot(items, root);
        _setLogRoot(set, root);
        _setLogRoot(list, root);
        _setLogRoot(map, root);
        _setLogRoot(set2, root);
        _setLogRoot(list2, root);
        _setLogRoot(map2, root);
    }

    @Override
    public JSONObject encode() {
        JSONObject json = new JSONObject();

        json.put("id" , this.id.getValue());
        json.put("name" , this.name.getValue());
        json.put("roleType" , this.roleType.getValue());
        json.put("b" , this.b.getValue());
        json.put("s" , this.s.getValue());
        json.put("i" , this.i.getValue());
        json.put("f" , this.f.getValue());
        json.put("d" , this.d.getValue());

        ItemEntity $item = this.item.getValue();
        if ($item != null) {
            json.put("item" , $item.encode());
        }

        JSONObject $items = new JSONObject();
        for (Integer $items$Key : this.items.keySet()) {
            $items.put(String.valueOf($items$Key), this.items.get($items$Key).encode());
        }
        json.put("items" , $items);

        JSONArray $set = new JSONArray();
        $set.addAll(this.set);
        json.put("set" , $set);

        JSONArray $list = new JSONArray();
        $list.addAll(this.list);
        json.put("list" , $list);

        JSONObject $map = new JSONObject();
        for (Integer $map$Key : this.map.keySet()) {
            $map.put(String.valueOf($map$Key), this.map.get($map$Key));
        }
        json.put("map" , $map);

        JSONArray $set2 = new JSONArray();
        for (ItemEntity $set2$Value : this.set2) {
            $set2.add($set2$Value.encode());
        }
        json.put("set2" , $set2);

        JSONArray $list2 = new JSONArray();
        for (ItemEntity $list2$Value : this.list2) {
            $list2.add($list2$Value.encode());
        }
        json.put("list2" , $list2);

        return json;
    }

    @Override
    public void decode(JSONObject json) {
        this.id.setValue(json.getLongValue("id" ));
        this.name.setValue(json.getOrDefault("name" , "" ).toString());
        this.roleType.setValue(json.getIntValue("roleType" ));
        this.b.setValue(json.getBooleanValue("b" ));
        this.s.setValue(json.getShortValue("s" ));
        this.i.setValue(json.getIntValue("i" ));
        this.f.setValue(json.getFloatValue("f" ));
        this.d.setValue(json.getDoubleValue("d" ));

        JSONObject $item = json.getJSONObject("item" );
        if ($item != null) {
            ItemEntity $item$Value = this.item.getValue();
            if ($item$Value == null) {
                $item$Value = new ItemEntity();
                this.item.setValue($item$Value);
            }
            $item$Value.decode($item);
        }

        JSONObject $items$1 = json.getJSONObject("items" );
        if ($items$1 != null) {
            PMap<Integer, ItemEntity> $items$2 = Empty.map();
            for (String $items$Key : $items$1.keySet()) {
                ItemEntity $items$Value = new ItemEntity();
                $items$Value.decode($items$1.getJSONObject($items$Key));
                $items$2 = $items$2.plus(Integer.valueOf($items$Key), $items$Value);
            }
            this.items.setValue($items$2);
        }

        JSONArray $set$1 = json.getJSONArray("set" );
        if ($set$1 != null) {
            PSet<Boolean> $set$2 = Empty.set();
            for (int i = 0; i < $set$1.size(); i++) {
                $set$2 = $set$2.plus($set$1.getBoolean(i));
            }
            this.set.setValue($set$2);
        }

        JSONArray $list$1 = json.getJSONArray("list" );
        if ($list$1 != null) {
            PVector<String> $list$2 = Empty.vector();
            for (int i = 0; i < $list$1.size(); i++) {
                $list$2 = $list$2.plus($list$1.getString(i));
            }
            this.list.setValue($list$2);
        }

        JSONObject $map$1 = json.getJSONObject("map" );
        if ($map$1 != null) {
            PMap<Integer, Integer> $map$2 = Empty.map();
            for (String $map$Key : $map$1.keySet()) {
                $map$2 = $map$2.plus(Integer.valueOf($map$Key), $map$1.getInteger($map$Key));
            }
            this.map.setValue($map$2);
        }

        JSONArray $set2$1 = json.getJSONArray("set2" );
        if ($set2$1 != null) {
            PSet<ItemEntity> $set2$2 = Empty.set();
            for (int i = 0; i < $set2$1.size(); i++) {
                ItemEntity $set2$Value = new ItemEntity();
                $set2$Value.decode($set2$1.getJSONObject(i));
                $set2$2 = $set2$2.plus($set2$Value);
            }
            this.set2.setValue($set2$2);
        }

        JSONArray $list2$1 = json.getJSONArray("list2" );
        if ($list2$1 != null) {
            PVector<ItemEntity> $list2$2 = Empty.vector();
            for (int i = 0; i < $list2$1.size(); i++) {
                ItemEntity $list2$Value = new ItemEntity();
                $list2$Value.decode($list2$1.getJSONObject(i));
                $list2$2 = $list2$2.plus($list2$Value);
            }
            this.list2.setValue($list2$2);
        }
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",roleType=" + RoleType.valueOf(roleType.getValue()) +
                ",b=" + b +
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

    public synchronized static void setTable(Table<Long, RoleData> table) {
        if (_table != null && _table.isWorkable()) {
            throw new IllegalStateException("数据已设置缓存表" );
        }
        Objects.requireNonNull(table, "参数[table]不能为空" );
        table.checkWorkable();
        _table = table;
    }

    private synchronized static void checkTable() {
        if (_table != null && _table.isWorkable()) {
            return;
        }

        Database database = Database.getDefault();
        if (database == null) {
            throw new IllegalStateException("没有默认数据库" );
        }

        if (_table == null) {
            _table = new Table<>("RoleData" , RoleData::new);
        }
        database.registerTable(_table);
    }

    public static RoleData get(Long id) {
        checkTable();
        return _table.get(id);
    }

    public static void delete(Long id) {
        checkTable();
        _table.delete(id);
    }

    public static void insert(RoleData data) {
        checkTable();
        _table.insert(data);
    }

    public static RoleData getOrInsert(Long id) {
        checkTable();
        return _table.getOrInsert(id);
    }

    public static void save(Long id) {
        checkTable();
        _table.save(id);
    }

}