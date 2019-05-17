package quan.transaction.log;

import quan.transaction.BeanData;
import quan.transaction.MappingData;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class RootLog implements Log {

    private BeanData beanData;

    private MappingData root;

    public RootLog(BeanData beanData, MappingData root) {
        this.root = root;
        this.beanData = beanData;
    }

    public MappingData getRoot() {
        return root;
    }

    public RootLog setRoot(MappingData root) {
        this.root = root;
        return this;
    }

    public BeanData getBeanData() {
        return beanData;
    }

    @Override
    public void commit() {
        beanData.setRoot(root);
    }
}
