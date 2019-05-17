package quan.transaction;

import quan.transaction.log.RootLog;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class BeanData extends Data {

    private MappingData root;

    @Override
    public final MappingData getRoot() {
        Transaction transaction = Transaction.current();
        if (transaction != null) {
            RootLog rootLog = transaction.getRootLog(this);
            if (rootLog != null) {
                return rootLog.getRoot();
            }
        }
        return root;
    }

    public final BeanData setRoot(MappingData root) {
        this.root = root;
        return this;
    }

    public final void addRootLog(MappingData root) {
        Transaction transaction = Transaction.current();
        RootLog rootLog = transaction.getRootLog(this);
        if (rootLog != null) {
            rootLog.setRoot(getRoot());
        } else {
            transaction.addRootLog(new RootLog(this, root));
        }

        addChildrenRootLog(root);
    }

    protected abstract void addChildrenRootLog(MappingData root);
}
