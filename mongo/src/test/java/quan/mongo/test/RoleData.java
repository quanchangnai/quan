package quan.mongo.test;

import quan.mongo.ListWrapper;

import java.util.List;

/**
 * 实际是生成的
 * Created by quanchangnai on 2018/8/6.
 */
public class RoleData extends BaseData {

    private ListWrapper<ItemData> items = new ListWrapper<>();

    public RoleData() {
        items.setMappingData(getMappingData());
    }

    @Override
    public void commit() {
        items.commit();
    }

    @Override
    public void rollback() {
        items.rollback();
    }

    public List<ItemData> getItems() {
        return items;
    }


}
