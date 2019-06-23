package quan.database.role;

import quan.database.Data;
import java.util.*;
import quan.database.field.*;
import quan.database.item.ItemBean;

/**
 * 角色
 * Created by 自动生成
 */
public class RoleData extends Data<Long> {

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

    @Override
    public Long primaryKey() {
        return getId();
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
                '}';

    }

}
