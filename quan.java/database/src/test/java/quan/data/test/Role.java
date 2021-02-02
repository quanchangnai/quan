package quan.data.test;

import quan.data.Transaction;
import quan.data.Transactional;
import quan.data.item.ItemEntity;
import quan.data.item.ItemEntity2;
import quan.data.role.RoleData;
import quan.data.role.RoleData2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by quanchangnai on 2020/4/20.
 */
@Transactional
public class Role {

    private long roleId;

    private RoleData roleData1 = new RoleData();

    private RoleData roleData2 = new RoleData();

    private RoleData2 roleData3 = new RoleData2(3L);

    private Random random = new Random();

    public Role() {
    }

    public Role(long roleId) {
        this.roleId = roleId;
    }

    public RoleData getRoleData1() {
        return roleData1;
    }

    public void reset() {
        roleId = 0;
        roleData1 = new RoleData();
        roleData2 = new RoleData();
        roleData3 = new RoleData2(3L);
    }

    @Transactional
    public void test1() {
//        if (roleId > 0) {
//            System.err.println("Role.test1(),roleId:" + roleId);
//        }
//
//        roleData1.setId(1L);
//        roleData1.setName("aaa");
//        roleData1.setI(random.nextInt());
//        roleData1.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData1.getList().add("aaa" + i);
            roleData1.getMap().put(i, i + random.nextInt());
            roleData1.getItems().put(i, new ItemEntity(i, "item" + i, new ArrayList<>()));
        }

        for (String s : roleData1.getList()) {
            if (s.equals("aaa5")) {
//                roleData1.getList().remove(2);
            }
        }


        for (Integer k : roleData1.getMap().keySet()) {
            if (k == 5) {
//                roleData1.getMap().remove(3);
            }
        }

//        ItemEntity itemEntity1 = new ItemEntity();
//        itemEntity1.setId(random.nextInt());
//        itemEntity1.setName("item111");
//
//        roleData1.setItem(itemEntity1);
//        roleData1.setItem(null);

//        Transaction.rollback();
    }

    public void test2() {
        if (roleId > 0) {
            System.err.println("Role.test2(),roleId:" + roleId);
        }

        roleData2.setId(2L);
        roleData2.setName("aaa");
        roleData2.setI(random.nextInt());
        roleData2.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData2.getList().add("aaa" + i);
            roleData2.getMap().put(i, i + random.nextInt());
            roleData2.getItems().put(i, new ItemEntity(i, "item" + i, new ArrayList<>()));
        }

        for (String s : roleData2.getList()) {
            if (s.equals("aaa5")) {
//                roleData2.getList().remove(2);
            }
        }

        for (Integer k : roleData2.getMap().keySet()) {
            if (k == 5) {
//                roleData2.getMap().remove(3);
            }
        }

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(random.nextInt());
        itemEntity1.setName("item111");

        roleData2.setItem(itemEntity1);
        roleData2.setItem(null);

    }

    public void test3() {
        if (roleId > 0) {
            System.err.println("Role.test3(),roleId:" + roleId);
        }

        roleData3.setName("aaa");
        roleData3.setI(random.nextInt());
        roleData3.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData3.getList().add("aaa" + i);
            roleData3.getMap().put(i, i + random.nextInt());
            roleData3.getItems().put(i, new ItemEntity2(i, "item" + i, new ArrayList<>()));
        }

        for (String s : roleData3.getList()) {
            if (s.equals("aaa5")) {
//                roleData3.getList().remove(2);
            }
        }

        for (Integer k : roleData3.getMap().keySet()) {
            if (k == 5) {
//                roleData3.getMap().remove(3);
            }
        }


        ItemEntity2 itemEntity1 = new ItemEntity2();
        itemEntity1.setId(random.nextInt());
        itemEntity1.setName("item111");

        roleData3.setItem(itemEntity1);
        roleData3.setItem(null);

    }

    @Transactional
    public void test4() {
        System.err.println("Role.test4()");

        roleData1.setName("test4");
        roleData1.setI(random.nextInt());
        roleData1.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData1.getList().add("aaa" + i);
            roleData1.getMap().put(i, i + random.nextInt());
            roleData1.getItems().put(i, new ItemEntity(i, "item" + i, new ArrayList<>()));
        }

        Transaction.onSucceeded(() -> System.err.println("test4 succeeded"));
        Transaction.onFailed(() -> System.err.println("test4 failed"));
        Transaction.onFinished(() -> System.err.println("test4 finished"));

        test5();

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(random.nextInt());
        itemEntity1.setName("item111");

        roleData1.setItem(itemEntity1);

        System.err.println("test4:" + roleData1);
        Transaction.rollback();
    }

    @Transactional(nested = true)
    public void test5() {
        System.err.println("Role.test5()");
        System.err.println("test5:" + roleData1);

        roleData1.setName("test5");
        roleData1.setI(random.nextInt());
        roleData1.getSet().clear();

        roleData1.getItems().clear();

        Transaction.onSucceeded(() -> System.err.println("test5 succeeded"));
        Transaction.onFailed(() -> System.err.println("test5 failed"));
        Transaction.onFinished(() -> System.err.println("test5 finished"));

        test6(1);

        roleData1.setItem(null);

//        Transaction.rollback();
    }

    @Transactional(nested = true)
    public void test6(int i) {
        System.err.println("Role.test6():" + i);
        System.err.println("test6:" + roleData1);

        roleData1.setName("test6");

        Transaction.onSucceeded(() -> System.err.println("test6 succeeded:" + i));
        Transaction.onFailed(() -> System.err.println("test6 failed:" + i));
        Transaction.onFinished(() -> System.err.println("test6 finished:" + i));

//        Transaction.rollback();

//        throw new RuntimeException("test6:" + i);

        test6(i + 1);

    }


    public void print() {
        System.err.println("list.size:");
        System.err.println(roleData1.getList().size());
        System.err.println(roleData2.getList().size());
        System.err.println(roleData3.getList().size());
    }
}
