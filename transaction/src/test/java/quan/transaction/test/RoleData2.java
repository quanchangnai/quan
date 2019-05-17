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
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            IntLog fieldLog = (IntLog) transaction.getFieldLog(this.id);
            if (fieldLog != null) {
                return fieldLog.getValue();
            }
        }

        return id.getValue();
    }

    public RoleData2 setId(int id) {
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

    public RoleData2 setName(String name) {
        onWriteData(null);
        Transaction transaction = Transaction.current();
        StringLog log = (StringLog) transaction.getFieldLog(this.name);
        if (log != null) {
            log.setValue(name);
        } else {
            transaction.addFieldLog(new StringLog(this.name, name));
        }
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
