package quan.database.test;

import com.alibaba.fastjson.JSONObject;
import quan.database.Transaction;
import quan.database.item.ItemEntity;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class TransactionTest {

    private static RoleData roleData1 = new RoleData(1L);

    public static void main(String[] args) {


        Transaction.execute(TransactionTest::update1);
        System.err.println("update1:" + roleData1);

        try {
            Transaction.execute(TransactionTest::update2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("update2:" + roleData1);

        JSONObject jsonObject = roleData1.encode();
        System.err.println("roleData1:" + jsonObject);

        RoleData roleData2 = new RoleData(0L);
        roleData2.decode(jsonObject);
        System.err.println("roleData2:" + roleData2);

    }

    private static boolean update1() {
        roleData1.setName("aaa");
        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setId(1);
        itemEntity1.setName("1");
        roleData1.setItem(itemEntity1);

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setId(100);
        itemEntity2.setName("111");
        roleData1.getItems().put(itemEntity2.getId(), itemEntity2);

        return true;
    }

    private static boolean update2() {
        roleData1.getMap().put(111, 222);
        return true;
    }
}
