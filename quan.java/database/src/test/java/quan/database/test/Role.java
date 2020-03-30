package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.Transactional;
import quan.database.item.ItemEntity;
import quan.database.role.RoleData;
import quan.database.role.RoleType;

/**
 * Created by quanchangnai on 2019/7/2.
 */
public class Role {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RoleData roleData;

    @Transactional
    public boolean login1() {
        System.err.println("login1======================================================" );
        logger.error("currentThread:{}", Thread.currentThread());
        new Exception().printStackTrace();
        return login2("1111" );
    }

    @Transactional
    public boolean login2(String password) {
        System.err.println("login2======================================================" );
        roleData = RoleData.getOrInsert(1L);
        logger.error("roleData:{}", roleData);

        roleData.setName("role" + System.currentTimeMillis());//role1562131925281
        roleData.setRoleType(RoleType.type1);

        roleData.getList().add(System.currentTimeMillis() + "" );
        roleData.getSet().add(true);
        roleData.getMap().put(1, 111);

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(111);
        itemEntity1.setName("111" );
        roleData.setItem(itemEntity1);

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setId(222);
        itemEntity2.setName("222" );
        roleData.getItems().put(itemEntity2.getId(), itemEntity2);

        ItemEntity itemEntity3 = new ItemEntity();
        itemEntity3.setId(333);
        itemEntity3.setName("333" );
        roleData.getList2().add(itemEntity3);


        ItemEntity itemEntity4 = new ItemEntity();
        itemEntity4.setId(444);
        itemEntity4.setName("444" );
        roleData.getSet2().add(itemEntity4);

        ItemEntity itemEntity5 = new ItemEntity();
        itemEntity5.setId(555);
        itemEntity5.setName("555" );
        roleData.getMap2().put(itemEntity5.getId(), itemEntity5);

        logger.error("roleData.setName:{}", roleData);

        System.err.println("roleData.encode():" + roleData.encode());

        logger.error("currentThread:{}", Thread.currentThread());


        if (roleData.getId() == 1) {
//            Transaction.breakdown();
        }

        if (roleData.getId() == 1) {
//            return false;
        }
        return true;
    }

    @Transactional
    public String login3() {
        System.err.println("login3======================================================" );
        RoleData.delete(1L);
        roleData = RoleData.getOrInsert(1L);
        logger.error("roleData:{}", roleData);

        return "login3";
    }

    @Transactional
    public static void login4() {
        System.err.println("login4======================================================" );
        System.err.println("roleData:" + RoleData.getOrInsert(1L));

    }

    public void update() {
        System.err.println("update======================================================" + Thread.currentThread());
//        new Exception().printStackTrace();
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleData=" + roleData +
                '}';
    }
}
