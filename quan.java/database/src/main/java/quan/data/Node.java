package quan.data;

/**
 * 数据节点
 * Created by quanchangnai on 2019/6/24.
 */
public abstract class Node extends Loggable {

    private Data<?> root;

    protected void _setRoot(Data<?> root) {
        this.root = root;
    }

    protected Data<?> _getRoot() {
        return root;
    }

    /**
     * 提交节点的根
     */
    protected void commit(Data<?> root) {
        this.root = root;
    }

    protected Data<?> _getLogRoot() {
        return _getLogRoot(Transaction.get());
    }

    protected Data<?> _getLogRoot(Transaction transaction) {
        if (transaction != null) {
            Data<?> root = _getRootLog(transaction, this);
            if (root != null) {
                return root;
            }
        }
        return root;
    }

    protected void _setLogRoot(Data<?> root) {
        Transaction transaction = Transaction.get(true);
        _setRootLog(transaction, this, root);
        _setChildrenLogRoot(root);
    }

    protected abstract void _setChildrenLogRoot(Data<?> root);

    protected static void _setRoot(Node node, Data<?> root) {
        node._setRoot(root);
    }

    protected static void _setLogRoot(Node node, Data<?> root) {
        if (node != null) {
            node._setLogRoot(root);
        }
    }

}
