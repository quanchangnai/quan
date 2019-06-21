package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.log.RootLog;

import java.util.Arrays;
import java.util.List;

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

    protected abstract void setChildrenLogRoot(Data root);

    protected void validValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("不允许添加null");
        }

        List<Class<?>> allowedClasses = Arrays.asList(Byte.class, Boolean.class, Short.class, Integer.class, Long.class, Double.class, String.class);
        if (!allowedClasses.contains(value.getClass()) && !Bean.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("不允许添加该数据类型：" + value.getClass() + "，允许的类型:" + Bean.class + " " + allowedClasses);
        }

        if (value instanceof Bean) {
            Data valueRoot = ((Bean) value).getRoot();
            if (valueRoot != null) {
                throw new IllegalArgumentException("添加的" + value.getClass().getSimpleName() + "当前正受到其它" + Data.class.getSimpleName() + "管理:" + valueRoot);
            }
        }

    }

}
