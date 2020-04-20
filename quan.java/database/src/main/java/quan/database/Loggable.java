package quan.database;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2020/4/8.
 */
public abstract class Loggable {

    protected static void _setFieldLog(Transaction transaction, Field field, Object log, Data<?> data) {
        transaction.setFieldLog(field, log, data);
    }

    protected static Object _getFieldLog(Transaction transaction, Field field) {
        return transaction.getFieldLog(field);
    }

    protected static void _setRootLog(Transaction transaction, Node node, Data<?> root) {
        if (node != null) {
            transaction.setRootLog(node, root);
        }
    }

    protected static Data<?> _getRootLog(Transaction transaction, Node node) {
        return transaction.getRootLog(node);
    }

}
