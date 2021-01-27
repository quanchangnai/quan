package quan.data.test;

import quan.data.Transaction;
import quan.data.Transactional;
import quan.data.item.ItemEntity;
import quan.data.role.RoleData;

import java.util.ArrayList;

/**
 * Created by quanchangnai on 2020/4/20.
 */
@Transactional
public class Role {

    private long roleId;

    private RoleData roleData1 = new RoleData();

    private RoleData roleData2 = new RoleData();

    public Role() {
    }

    public Role(long roleId) {
        this.roleId = roleId;
        System.err.println("RoleTest(long roleId)");
    }

    public Role(Long roleId) {
        this.roleId = roleId;
        System.err.println("RoleTest(Long roleId)");
    }

    @Transactional
    public int test1(int v) {
        System.err.println("RoleTest.test1()===============" + roleId + "=" + v);

        roleData1.setId(1L);
        roleData1.setName("aaa");
        roleData1.setI(1);
//        roleData1.getSet().add(true);

        for (int i = 0; i < 10; i++) {
            roleData1.getList().add("aaa" + i);
            roleData1.getMap().put(i, i);
            roleData1.getItems().put(i, new ItemEntity(i, "item" + i, new ArrayList<>()));
        }

        for (String s : roleData1.getList()) {
            if (s.equals("aaa5")) {
//                roleData1.getList().remove(2);
            }
        }

        for (Integer k : roleData1.getMap().keySet()) {
            if (k == 5) {
                roleData1.getMap().remove(3);
            }
        }

        System.err.println("roleData1.getList().getClass():" + roleData1.getList().getClass());

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(111);
        itemEntity1.setName("item111");

//        roleData1.setItem(itemEntity1);
//        roleData1.setItem(null);

        roleData2.setId(2);
        roleData2.setName("bbb");
        roleData2.setItem(itemEntity1);

        for (int i = 0; i < 10; i++) {
            roleData2.getList().add("bbb" + i);
            roleData2.getMap().put(i, i);
        }

        System.err.println("roleData1:" + roleData1.toJson());
        System.err.println("roleData2:" + roleData2.toJson());

//        throw new RuntimeException("aaa");

        Transaction.onSucceeded(() -> System.err.println("onSucceeded"));
        Transaction.onFailed(() -> System.err.println("onFailed"));

        Transaction.rollback();

        return 1;
    }

    public void test2() {
        System.err.println("RoleTest.test2()===============" + roleId);

        System.err.println("roleData1:" + roleData1);
        System.err.println("roleData2:" + roleData2);
    }
}
