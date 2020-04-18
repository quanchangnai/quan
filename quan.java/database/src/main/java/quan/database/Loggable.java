package quan.database;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2020/4/8.
 */
public abstract class Loggable {

    protected abstract void commit(Object log);

    protected static void _setFieldLog(Transaction transaction, Field field, Object log, Data<?> root) {
        transaction.setFieldLog(field, log, root);
    }

    protected static Object _getFieldLog(Transaction transaction, Field field) {
        return transaction.getFieldLog(field);
    }

    protected static void _setNodeLog(Transaction transaction, Node node, Data<?> root) {
        transaction.setNodeLog(node, root);
    }

    protected static Data<?> _getNodeLog(Transaction transaction, Node node) {
        return transaction.getNodeLog(node);
    }

}
