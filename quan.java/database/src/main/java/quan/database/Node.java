package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据节点
 * Created by quanchangnai on 2019/6/24.
 */
public abstract class Node {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Data root;

    protected Data _getRoot() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            RootLog rootLog = transaction.getRootLog(this);
            if (rootLog != null) {
                return rootLog.getRoot();
            }
        }
        return root;
    }

    protected void _setRoot(Data root) {
        this.root = root;
    }

    protected void _setLogRoot(Data root) {
        Transaction transaction = Transaction.get();
        RootLog rootLog = transaction.getRootLog(this);
        if (rootLog != null) {
            rootLog.setRoot(root);
        } else {
            transaction.addRootLog(new RootLog(this, root));
        }

        _setChildrenLogRoot(root);
    }

    protected static void _setLogRoot(Node node, Data root) {
        node._setLogRoot(root);
    }

    protected abstract void _setChildrenLogRoot(Data root);

}
