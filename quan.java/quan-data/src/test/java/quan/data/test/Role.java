package quan.data.test;

import quan.data.Transaction;
import quan.data.Transactional;
import quan.data.item.ItemBean;
import quan.data.role.RoleData;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by quanchangnai on 2020/4/20.
 */
public class Role {

    private long roleId;

    private RoleData roleData1 = new RoleData(1);

    private RoleData roleData2 = new RoleData(2);

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
        roleData1 = new RoleData(1);
        roleData2 = new RoleData(2L);
    }

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
            roleData1.getItems().put(i, new ItemBean(i, "item" + i, new ArrayList<>()));
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

//        ItemBean ItemBean1 = new ItemBean();
//        ItemBean1.setId(random.nextInt());
//        ItemBean1.setName("item111");
//
//        roleData1.setItem(ItemBean1);
//        roleData1.setItem(null);

//        Transaction.rollback();
    }

    public void test2() {
        if (roleId > 0) {
            System.err.println("Role.test2(),roleId:" + roleId);
        }

        roleData2.setName("aaa");
        roleData2.setI(random.nextInt());
        roleData2.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData2.getList().add("aaa" + i);
            roleData2.getMap().put(i, i + random.nextInt());
            roleData2.getItems().put(i, new ItemBean(i, "item" + i, new ArrayList<>()));
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

        ItemBean ItemBean1 = new ItemBean();
        ItemBean1.setId(random.nextInt());
        ItemBean1.setName("item111");

        roleData2.setItem(ItemBean1);
        roleData2.setItem(null);

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
            roleData1.getItems().put(i, new ItemBean(i, "item" + i, new ArrayList<>()));
        }

        Transaction.onSucceeded(() -> System.err.println("test4 succeeded"));
        Transaction.onFailed(() -> System.err.println("test4 failed"));
        Transaction.onFinished(() -> System.err.println("test4 finished"));

        test5();

        ItemBean ItemBean1 = new ItemBean();
        ItemBean1.setId(random.nextInt());
        ItemBean1.setName("item111");

        roleData1.setItem(ItemBean1);

        System.err.println("test4:" + roleData1);
        Transaction.rollback();
    }

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

        if (i < 8) {
            test6(i + 1);
        }
    }


    public void print() {
        System.err.println("list.size:");
        System.err.println(roleData1.getList().size());
        System.err.println(roleData2.getList().size());
    }
}
