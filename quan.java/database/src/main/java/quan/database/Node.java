package quan.database;

import quan.database.log.RootLog;

/**
 * 数据节点
 * Created by quanchangnai on 2019/6/24.
 */
public abstract class Node extends LogAccessor {

    private Data root;

    protected void _setRoot(Data root) {
        this.root = root;
    }

    protected Data _getLogRoot() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            RootLog rootLog = _getRootLog(transaction, this);
            if (rootLog != null) {
                return rootLog.getRoot();
            }
        }
        return root;
    }

    protected void _setLogRoot(Data root) {
        Transaction transaction = Transaction.get(true);
        RootLog rootLog = _getRootLog(transaction, this);
        if (rootLog != null) {
            rootLog.setRoot(root);
        } else {
            _addRootLog(transaction, new RootLog(this, root));
        }

        _setChildrenLogRoot(root);
    }

    protected static void _setLogRoot(Node node, Data root) {
        node._setLogRoot(root);
    }

    protected abstract void _setChildrenLogRoot(Data root);


    public abstract static class Setter {

        protected static void _setRoot(Node node, Data root) {
            node._setRoot(root);
        }

    }

}
