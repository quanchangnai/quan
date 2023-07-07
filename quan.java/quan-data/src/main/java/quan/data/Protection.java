package quan.data;

import quan.data.field.Field;


/**
 * 本类的所有方法都是受保护的
 */
public abstract class Protection {

    protected static void _setFieldLog(Transaction transaction, Field field, Object log, Data<?> owner, int position) {
        transaction.setFieldLog(field, log, owner, position);
    }

    protected static Object _getFieldLog(Transaction transaction, Field field) {
        return transaction.getFieldLog(field);
    }

    protected static void _setNodeLog(Transaction transaction, Node node, Data<?> owner, int position) {
        if (node != null) {
            transaction.setNodeLog(node, owner, position);
        }
    }

    protected static Node.Log _getNodeLog(Transaction transaction, Node node) {
        return transaction.getNodeLog(node);
    }

    protected static void _setNodeOwner(Node node, Data<?> owner, int position) {
        if (node != null) {
            node._setOwner(owner, position, true);
        }
    }

    protected static void _setDataUpdatedField(Data<?> data, int field) {
        if (data != null && field > 0) {
            data._updatedFields.set(field);
        }
    }

}
