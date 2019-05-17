package quan.transaction.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.transaction.Transaction;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class Role {

    private final Logger logger = LogManager.getLogger(Role.class);

    private RoleData roleData = new RoleData();

    public RoleData getRoleData() {
        return roleData;
    }

    public void test1() {
        roleData.setId(roleData.getId() + 10);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        roleData.setName("aaa");

        String tid = "";
        Transaction current = Transaction.current();
        if (current != null) {
            tid += current.getId();
        }

        logger.error("事务{}:{}", tid, roleData);

        if (roleData.getId() > 20) {
            throw new RuntimeException();
        }

    }

}
