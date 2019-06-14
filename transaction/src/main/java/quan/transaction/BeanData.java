package quan.transaction;

import quan.transaction.log.RootLog;

import java.util.Arrays;
import java.util.List;

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

    public final void setLogRoot(MappingData root) {
        Transaction transaction = Transaction.current();
        RootLog rootLog = transaction.getRootLog(this);
        if (rootLog != null) {
            rootLog.setRoot(root);
        } else {
            transaction.addRootLog(new RootLog(this, root));
        }

        setChildrenLogRoot(root);
    }

    protected abstract void setChildrenLogRoot(MappingData root);

    protected void validValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("不允许添加null");
        }

        List<Class<?>> allowedClasses = Arrays.asList(Byte.class, Boolean.class, Short.class, Integer.class, Long.class, Double.class, String.class);
        if (!allowedClasses.contains(value.getClass()) && !BeanData.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("不允许添加该数据类型：" + value.getClass() + "，允许的类型:" + BeanData.class + " " + allowedClasses);
        }

        if (value instanceof BeanData) {
            MappingData valueRoot = ((BeanData) value).getRoot();
            if (valueRoot != null) {
                throw new IllegalArgumentException("添加的" + value.getClass().getSimpleName() + "当前正受到其它" + MappingData.class.getSimpleName() + "管理:" + valueRoot);
            }
        }

    }

}
