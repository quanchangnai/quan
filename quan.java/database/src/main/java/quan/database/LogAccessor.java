package quan.database;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2020/4/8.
 */
public class LogAccessor {

    protected static void _addFieldLog(Transaction transaction, Field field, Object value, Data root) {
        transaction.addFieldLog(field, value, root);
    }

    protected static Object _getFieldLog(Transaction transaction, Field field) {
        return transaction.getFieldLog(field);
    }

    protected static void _addRootLog(Transaction transaction, Node node, Data root) {
        transaction.addRootLog(node, root);
    }

    protected static Data _getRootLog(Transaction transaction, Node node) {
        return transaction.getRootLog(node);
    }

}
