package quan.transaction.test;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.field.BeanField;
import quan.transaction.field.IntField;
import quan.transaction.field.StringField;
import quan.transaction.log.BeanLog;
import quan.transaction.log.IntLog;
import quan.transaction.log.StringLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class RoleData extends MappingData {

    private IntField id = new IntField();

    private StringField name = new StringField();

    private BeanField<ItemData> itemData = new BeanField<>();

    public int getId() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            IntLog log = (IntLog) transaction.getFieldLog(this.id);
            if (log != null) {
                return log.getValue();
            }
        }

        return id.getValue();
    }

    public RoleData setId(int id) {
        onWriteData(null);
        Transaction transaction = Transaction.current();
        IntLog log = (IntLog) transaction.getFieldLog(this.id);
        if (log != null) {
            log.setValue(id);
        } else {
            transaction.addFieldLog(new IntLog(this.id, id));
        }
        return this;
    }

    public String getName() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            StringLog log = (StringLog) transaction.getFieldLog(this.name);
            if (log != null) {
                return log.getValue();
            }
        }
        return name.getValue();
    }

    public RoleData setName(String name) {
        onWriteData(null);
        Transaction transaction = Transaction.current();
        StringLog log = (StringLog) transaction.getFieldLog(this.name);
        if (log != null) {
            log.setValue(name);
        } else {
            transaction.addFieldLog( new StringLog(this.name, name));
        }
        return this;
    }

    public ItemData getItemData() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            BeanLog<ItemData> log = (BeanLog<ItemData>) transaction.getFieldLog(this.itemData);
            if (log != null) {
                return log.getValue();
            }
        }
        return itemData.getValue();
    }

    public RoleData setItemData(ItemData itemData) {
        onWriteData(itemData);
        Transaction transaction = Transaction.current();
        BeanLog<ItemData> log = (BeanLog<ItemData>) transaction.getFieldLog(this.itemData);
        if (log != null) {
            log.setValue(itemData);
        } else {
            transaction.addFieldLog(new BeanLog<>(this.itemData, itemData));
        }

        itemData.addRootLog(getRoot());

        return this;
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "version=" + getVersion() +
                ",id=" + getId() +
                ", name=" + getName() +
                ", itemData=" + getItemData() +
                '}';
    }
}
