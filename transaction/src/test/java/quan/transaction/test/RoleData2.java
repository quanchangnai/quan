package quan.transaction.test;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.field.IntField;
import quan.transaction.field.StringField;
import quan.transaction.log.IntLog;
import quan.transaction.log.StringLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class RoleData2 extends MappingData {

    private IntField id = new IntField();

    private StringField name = new StringField();


    public int getId() {
        return id.getValue();
    }

    public RoleData2 setId(int id) {
        this.id.setLogValue(id,getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public RoleData2 setName(String name) {
        this.name.setLogValue(name,getRoot());
        return this;
    }


    @Override
    public String toString() {
        return "RoleData2{" +
                "version=" + getVersion() +
                ",id=" + getId() +
                ", name=" + getName() +
                '}';
    }
}
