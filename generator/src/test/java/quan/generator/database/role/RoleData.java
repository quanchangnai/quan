package quan.generator.database.role;

import quan.database.Data;
import quan.generator.database.item.ItemBean;
import java.util.*;
import quan.database.field.*;

/**
 * 角色
 * Created by 自动生成
 */
public class RoleData extends Data<Long> {

    private BaseField<Long> roleId = new BaseField<>();//角色ID

    private BaseField<String> roleName = new BaseField<>();

    private BeanField<ItemBean> itemBean = new BeanField<>();

    private MapField<Integer, Integer> items = new MapField<>(getRoot());



    public long getRoleId() {
        return roleId.getValue();
    }

    public void setRoleId(long roleId) {
        this.roleId.setLogValue(roleId, getRoot());
    }

    public String getRoleName() {
        return roleName.getValue();
    }

    public void setRoleName(String roleName) {
        this.roleName.setLogValue(roleName, getRoot());
    }

    public ItemBean getItemBean() {
        return itemBean.getValue();
    }

    public void setItemBean(ItemBean itemBean) {
        this.itemBean.setLogValue(itemBean, getRoot());
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }


    @Override
    public Long primaryKey() {
        return getRoleId();
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "roleId=" + roleId +
                ",roleName='" + roleName + '\'' +
                ",itemBean=" + itemBean +
                ",items=" + items +
                '}';

    }

}
