package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.Transaction;
import quan.database.Transactional;
import quan.database.item.ItemBean;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/7/2.
 */
public class Role {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RoleData roleData;

    @Transactional
    public boolean login1() {
        System.err.println("login1======================================================");
        logger.error("currentThread:{}", Thread.currentThread());
        return login2("1111");
    }

    @Transactional
    public boolean login2(String password) {
        System.err.println("login2======================================================");
        roleData = RoleData.getOrInsert(1L);
        logger.error("roleData:{}", roleData);

        roleData.setName("role" + System.currentTimeMillis());//role1562131925281
        roleData.getList().add(System.currentTimeMillis() + "");
        roleData.getSet().add(true);
        roleData.getMap().put(1, 111);

        ItemBean itemBean1 = new ItemBean();
        itemBean1.setId(111);
        itemBean1.setName("111");
        roleData.setItem(itemBean1);

        ItemBean itemBean2 = new ItemBean();
        itemBean2.setId(222);
        itemBean2.setName("222");
        roleData.getItems().put(itemBean2.getId(), itemBean2);

        ItemBean itemBean3 = new ItemBean();
        itemBean3.setId(333);
        itemBean3.setName("333");
        roleData.getList2().add(itemBean3);


        ItemBean itemBean4 = new ItemBean();
        itemBean4.setId(444);
        itemBean4.setName("444");
        roleData.getSet2().add(itemBean4);

        ItemBean itemBean5 = new ItemBean();
        itemBean5.setId(555);
        itemBean5.setName("555");
        roleData.getMap2().put(itemBean5.getId(), itemBean5);

        logger.error("roleData.setName:{}", roleData);

        System.err.println("roleData.encode():"+roleData.encode());

        logger.error("currentThread:{}", Thread.currentThread());


        if (roleData.getId() == 1) {
            Transaction.breakdown();
        }


        if (roleData.getId() == 1) {
//            return false;
        }

        return true;
    }

    @Transactional
    public String login3() {
        System.err.println("login3======================================================");
        roleData = RoleData.getOrInsert(1L);
        logger.error("roleData:{}", roleData);

        return "login3";
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleData=" + roleData +
                '}';
    }
}
