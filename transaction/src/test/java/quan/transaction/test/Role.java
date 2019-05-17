package quan.transaction.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quan.transaction.Transaction;
import quan.transaction.Transactional;

/**
 * Created by quanchangnai on 2019/5/17.
 */
@Transactional
public class Role {

    private final Logger logger = LogManager.getLogger(Role.class);

    private RoleData roleData = new RoleData();
    private RoleData2 roleData2 = new RoleData2();

    public RoleData getRoleData() {
        return roleData;
    }

    @Transactional
    public void test1() {
        String tid = "";
        Transaction current = Transaction.current();
        if (current != null) {
            tid += current.getId();
        }
        logger.error("事务开始{}:{}", tid, roleData);

        roleData2.setName("bbb");

        roleData.setId(roleData.getId() + 10);

//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        roleData.setName("aaa");

        roleData2.setId(roleData2.getId() + 1);

        if (roleData.getItemData() == null) {
            ItemData itemData = new ItemData();
            itemData.setId(1);
            roleData.setItemData(itemData);
        } else {
            roleData.getItemData().setId(roleData.getItemData().getId() + 1);
        }

        logger.error("事务结束{}:{}", tid, roleData);

        if (roleData.getId() > 20) {
            throw new RuntimeException();
        }

    }

    @Transactional
    public void test2() {
        if (roleData.getItemData() == null) {
            roleData.setItemData(new ItemData());
        } else {
            roleData.getItemData().setId(20);
        }

    }

}
