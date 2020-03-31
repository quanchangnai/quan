package quan.database.role;

import java.util.*;
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


    public RoleData(Long id) {
        this.id.setLogValue(id, _getRoot());
    }
  
    /**
     * 主键
     */
    @Override
    public Long _getId() {
        return id.getValue();
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
        this.roleType.setLogValue(roleType.value(), _getRoot());
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

}