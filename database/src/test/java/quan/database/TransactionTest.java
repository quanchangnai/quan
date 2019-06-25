package quan.database;

import com.alibaba.fastjson.JSONObject;
import quan.database.item.ItemBean;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class TransactionTest {

    private static RoleData roleData1 = new RoleData();

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

        RoleData roleData2 = new RoleData();
        roleData2.decode(jsonObject);
        System.err.println("roleData2:" + roleData2);
    }

    private static void update1() {
        roleData1.setId(111);
        roleData1.setName("aaa");
        ItemBean itemBean1 = new ItemBean();
        itemBean1.setId(1);
        itemBean1.setName("1");
        roleData1.setItemBean(itemBean1);

        ItemBean itemBean2 = new ItemBean();
        itemBean2.setId(100);
        itemBean2.setName("111");
        roleData1.getItems().put(itemBean2.getId(), itemBean2);
    }

    private static void update2() {
        roleData1.setId(222);
        roleData1.getMap().put(111, 222);
        Transaction.breakdown();
    }
}
