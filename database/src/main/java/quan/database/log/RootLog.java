package quan.database.log;

import quan.database.Bean;
import quan.database.Data;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class RootLog implements Log {

    private Bean bean;

    private Data root;

    public RootLog(Bean bean, Data root) {
        this.root = root;
        this.bean = bean;
    }

    public Data getRoot() {
        return root;
    }

    public RootLog setRoot(Data root) {
        this.root = root;
        return this;
    }

    public Bean getBean() {
        return bean;
    }

    @Override
    public void commit() {
        bean.setRoot(root);
    }
}
