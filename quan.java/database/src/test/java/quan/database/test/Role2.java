package quan.database.test;

import quan.database.Transactional;

/**
 * Created by quanchangnai on 2019/7/5.
 */
public class Role2 {

    @Transactional
    public boolean login() {
        System.err.println("Role2.login()");
        return true;
    }
}
