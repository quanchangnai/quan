package quan.transaction.test;

import quan.transaction.BeanData;
import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.field.IntField;
import quan.transaction.log.IntLog;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class ItemData extends BeanData {

    private IntField id = new IntField();

    public int getId() {
        return id.getValue();
    }

    public ItemData setId(int id) {
        this.id.setLogValue(id,getRoot());
        return this;
    }

    @Override
    protected void addChildrenRootLog(MappingData root) {
        //字段里面有bean需要添加RootLog
    }


    @Override
    public String toString() {
        return "ItemData{" +
                "id=" + id +
                '}';
    }
}
