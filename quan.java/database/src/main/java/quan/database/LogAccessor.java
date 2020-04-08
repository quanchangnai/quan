package quan.database;

import quan.database.field.Field;
import quan.database.log.FieldLog;
import quan.database.log.RootLog;

/**
 * Created by quanchangnai on 2020/4/8.
 */
public class LogAccessor {

    protected static void _addDataLog(Transaction transaction, Data data) {
        transaction.addDataLog(data);
    }

    protected static void _addFieldLog(Transaction transaction, FieldLog fieldLog) {
        transaction.addFieldLog(fieldLog);
    }

    protected static FieldLog _getFieldLog(Transaction transaction, Field field) {
        return transaction.getFieldLog(field);
    }

    protected static void _addRootLog(Transaction transaction, RootLog rootLog) {
        transaction.addRootLog(rootLog);
    }

    protected static RootLog _getRootLog(Transaction transaction, Node node) {
        return transaction.getRootLog(node);
    }

}
