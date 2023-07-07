package quan.data;

/**
 * 数据节点
 */
public abstract class Node extends Protection {

    /**
     * 拥有者，即所属的数据
     */
    private Data<?> owner;

    /**
     * 在数据中的位置，即数据的第几个字段
     */
    private int position;

    protected void _setOwner(Data<?> owner, int position, boolean updateField) {
        this.owner = owner;
        this.position = position;
        if (owner != null && updateField) {
            owner._updatedFields.set(position);
        }
    }

    protected Data<?> _getOwner() {
        return owner;
    }

    protected int _getPosition() {
        return position;
    }

    protected void commit(Log log) {
        this.owner = log.owner;
        this.position = log.position;
        if (owner != null) {
            owner._updatedFields.set(this.position);
        }
    }

    protected void _setLogOwner(Data<?> owner, int position) {
        _setNodeLog(Transaction.get(), this, owner, position);
        _setChildrenLogOwner(owner, position);
    }

    protected Data<?> _getLogOwner() {
        return _getLogOwner(Transaction.get());
    }

    protected Data<?> _getLogOwner(Transaction transaction) {
        if (transaction != null) {
            Log log = _getNodeLog(transaction, this);
            if (log != null) {
                return log.owner;
            }
        }
        return owner;
    }

    protected int _getLogPosition() {
        return _getLogPosition(Transaction.get());
    }

    protected int _getLogPosition(Transaction transaction) {
        if (transaction != null) {
            Log log = _getNodeLog(transaction, this);
            if (log != null) {
                return log.position;
            }
        }
        return position;
    }


    protected abstract void _setChildrenLogOwner(Data<?> owner, int position);

    protected static void _setOwner(Node node, Data<?> owner, int position) {
        node._setOwner(owner, position, true);
    }

    protected static void _setLogOwner(Node node, Data<?> owner, int position) {
        if (node != null) {
            node._setLogOwner(owner, position);
        }
    }

    static class Log {

        /**
         * @see Node#owner
         */
        Data<?> owner;

        /**
         * @see Node#position
         */
        int position;

    }

}
