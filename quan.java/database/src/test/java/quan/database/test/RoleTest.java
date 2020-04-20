package quan.database.test;

import quan.database.Transaction;
import quan.database.Transactional;
import quan.database.item.ItemEntity;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2020/4/20.
 */
public class RoleTest {

    private RoleData roleData1 = new RoleData(1L);

    private RoleData roleData2 = new RoleData(2L);

    @Transactional
    public int test1() {
        System.err.println("RoleTest.test1()===============");

        roleData1.setName("aaa");
        roleData1.setI(1);
        roleData1.getList().add("aaa");
        roleData1.getSet().add(true);
        roleData1.getMap().put(1, 1);

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(111);
        itemEntity1.setName("item111");

        roleData1.setItem(itemEntity1);
//        roleData1.setItem(null);

//        roleData2.setItem(itemEntity1);

        System.err.println("roleData1:" + roleData1);
        System.err.println("roleData2:" + roleData2);

//        throw new RuntimeException("aaa");

        Transaction.breakdown();

        return 1;
    }

    public void test2() {
        System.err.println("RoleTest.test2()===============");

        System.err.println("roleData1:" + roleData1);
        System.err.println("roleData2:" + roleData2);
    }
}
