package quan.transaction.test;

import quan.transaction.MappingData;
import quan.transaction.Transaction;
import quan.transaction.field.IntField;
import quan.transaction.field.StringField;
import quan.transaction.log.IntFieldLog;
import quan.transaction.log.StringFieldLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class RoleData extends MappingData {

    private IntField id = new IntField();

    private StringField name = new StringField();


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

    public RoleData setId(int id) {
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

    public String getName() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            StringFieldLog fieldLog = (StringFieldLog) transaction.getFieldLog(this.name);
            if (fieldLog != null) {
                return fieldLog.getValue();
            }
        }
        return name.getValue();
    }

    public RoleData setName(String name) {
        onWriteData(null);
        Transaction transaction = Transaction.current();
        StringFieldLog fieldLog = (StringFieldLog) transaction.getFieldLog(this.name);
        if (fieldLog != null) {
            fieldLog.setValue(name);
        } else {
            transaction.addFieldLog(new StringFieldLog(this.name, name));
        }
        return this;
    }


    @Override
    public String toString() {
        return "RoleData{" +
                "version=" + getVersion() +
                ",id=" + getId() +
                ", name=" + getName() +
                '}';
    }
}
