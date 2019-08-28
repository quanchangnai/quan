package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public abstract class Node {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Data root;

    public Data _getRoot() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            RootLog rootLog = transaction.getRootLog(this);
            if (rootLog != null) {
                return rootLog.getRoot();
            }
        }
        return root;
    }

    public void _setRoot(Data root) {
        this.root = root;
    }

    public void _setLogRoot(Data root) {
        Transaction transaction = Transaction.get();
        RootLog rootLog = transaction.getRootLog(this);
        if (rootLog != null) {
            rootLog.setRoot(root);
        } else {
            transaction.addRootLog(new RootLog(this, root));
        }

        _setChildrenLogRoot(root);
    }

    public abstract void _setChildrenLogRoot(Data root);


}
