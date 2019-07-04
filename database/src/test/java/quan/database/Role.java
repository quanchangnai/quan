package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.item.ItemBean;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/7/2.
 */
public class Role {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RoleData roleData;

    @Transactional
    public String login() {
        logger.error("currentThread:{}", Thread.currentThread());
        String result = login("1111");
        System.err.println("login(1111):" + result);
        return result+":xxxxxxx";
    }

    @Transactional
    public String login(String password) {
        System.err.println("login======================================================");
        roleData = RoleData.getOrInsert(1L);
        logger.error("RoleData.getOrInsert(1L):{}", roleData);

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

        RuntimeException runtimeException = new RuntimeException();

        if (roleData.getId() == 1) {
//            throw runtimeException;
        }

//        runtimeException.printStackTrace();

        if (roleData.getId() == 1) {
//            return false;
        }

        return "success";
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleData=" + roleData +
                '}';
    }
}
