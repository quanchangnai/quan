package quan.database;

import com.alibaba.fastjson.JSONObject;
import quan.database.item.ItemBean;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class TransactionTest {

    private static RoleData roleData = RoleData.cache.get(0L);

    public static void main(String[] args) {
        Transaction.execute(TransactionTest::update1);
        System.err.println("update1:" + roleData);

        try {
            Transaction.execute(TransactionTest::update2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("update2:" + roleData);

        JSONObject jsonObject = roleData.encode();
        System.err.println("roleData:" + jsonObject);

        RoleData roleData2 = RoleData.cache.get(0L);
        roleData2.decode(jsonObject);
        System.err.println("roleData2:" + roleData);
    }

    private static void update1() {
        roleData.setId(111);
        roleData.setName("aaa");
        ItemBean itemBean = new ItemBean();
        itemBean.setId(1);
        itemBean.setName("1");
        roleData.setItemBean(itemBean);

        ItemBean itemBean1 = new ItemBean();
        itemBean1.setId(100);
        itemBean1.setName("111");
        roleData.getItems().put(itemBean1.getId(), itemBean1);
    }

    private static void update2() {
        roleData.setId(222);
        roleData.getMap().put(111, 222);
        Transaction.breakdown();
    }
}
