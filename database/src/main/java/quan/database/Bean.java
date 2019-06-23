package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.log.RootLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Bean {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Data root;

    public Data getRoot() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            RootLog rootLog = transaction.getRootLog(this);
            if (rootLog != null) {
                return rootLog.getRoot();
            }
        }
        return root;
    }

    public final Bean setRoot(Data root) {
        this.root = root;
        return this;
    }

    public final void setLogRoot(Data root) {
        Transaction transaction = Transaction.current();
        RootLog rootLog = transaction.getRootLog(this);
        if (rootLog != null) {
            rootLog.setRoot(root);
        } else {
            transaction.addRootLog(new RootLog(this, root));
        }

        setChildrenLogRoot(root);
    }

    public void setChildrenLogRoot(Data root) {

    }

}
