package quan.transaction.test;

import quan.transaction.BeanData;
import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.field.IntField;
import quan.transaction.log.IntFieldLog;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class ItemData extends BeanData {

    private IntField id = new IntField();

    public int getId() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            IntFieldLog fieldLog = (IntFieldLog) transaction.getFieldLog(this.id);
            if (fieldLog != null) {
                return fieldLog.getValue();
            }
        }

        return id.getValue();
    }

    public ItemData setId(int id) {
        onWriteData(null);
        Transaction transaction = Transaction.current();
        IntFieldLog fieldLog = (IntFieldLog) transaction.getFieldLog(this.id);
        if (fieldLog != null) {
            fieldLog.setValue(id);
        } else {
            transaction.addFieldLog(new IntFieldLog(this.id, id));
        }
        return this;
    }

    @Override
    protected void addChildrenRootLog(MappingData root) {
        //字段里面有bean需要添加RootLog
    }


    @Override
    public String toString() {
        return "ItemData{" +
                "id=" + getId() +
                '}';
    }
}
