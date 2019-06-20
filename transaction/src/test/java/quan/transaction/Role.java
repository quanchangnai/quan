package quan.transaction;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * Created by quanchangnai on 2019/5/17.
 */
@Transactional
public class Role {

    private final Logger logger = LoggerFactory.getLogger(Role.class);

    private RoleData roleData = new RoleData();
    private RoleData2 roleData2 = new RoleData2();

    public RoleData getRoleData() {
        return roleData;
    }


    @Transactional
    public void test1() {
        long startTime = System.currentTimeMillis();
        String tid = "";
        Transaction current = Transaction.current();
        if (current != null) {
            tid += current.getId();
        }

//        logger.error("test1开始,事务ID:{},{}", tid, roleData);
//
//        roleData2.setName("bbb" + tid);
//
//        roleData.setId(roleData.getId() + 10);
//
//
//        roleData.setName("aaa" + roleData.getId());
//
//        roleData2.setId(roleData2.getId() + 1);
//
//        if (roleData.getItemData() == null) {
//            ItemData itemData = new ItemData();
//            itemData.setId(1);
//            roleData.setItemData(itemData);
//        } else {
//            roleData.getItemData().setId(roleData.getItemData().getId() + 1);
//        }
//
//        if (roleData.getId() == 10) {
//            roleData.setItemData(null);
//        }

//        if (roleData.getMap().isEmpty()) {
//            ItemData itemData = new ItemData();
//            itemData.setId(2);
//            roleData.getMap().put(itemData.getId(), itemData);
//        } else {
//            roleData.getMap().get(2).setId(roleData.getMap().get(2).getId() + 1);
//        }
//
//        roleData.getSet().add(roleData.getId());
        roleData.getList().add(roleData.getId());

        long endTime = System.currentTimeMillis();

        logger.error("test1结束:{},事务ID:{},{}", endTime-startTime, tid, roleData);

        if (roleData.getId() > 20) {
            List<Integer> subList = roleData.getList().subList(0, 2);
//            Transaction.breakdown();
        }

    }

    @Transactional
    public void test2() {
        String tid = "";
        Transaction current = Transaction.current();
        if (current != null) {
            tid += current.getId();
        }
        logger.error("test2开始，事务ID:{},{}", tid, roleData);

        ItemData itemData = roleData.getMap().get(2);

        roleData.getMap().clear();

        itemData.setId(1000);
        logger.error("itemData.getRoot():{}", itemData.getRoot());


        logger.error("test2结束，事务ID:{},{}", tid, roleData);
        Transaction.breakdown();
    }

}
