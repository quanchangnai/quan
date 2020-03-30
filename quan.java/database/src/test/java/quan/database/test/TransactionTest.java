package quan.database.test;

import com.alibaba.fastjson.JSONObject;
import quan.database.Transaction;
import quan.database.item.ItemEntity;
import quan.database.role.RoleData;
import quan.database.role.Role2Data;

import java.util.*;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class TransactionTest {

    private static RoleData roleData1;

    public static void main(String[] args) {

//        test1();

        Transaction.execute(TransactionTest::test2);
    }

    private static void test1() {
        roleData1 = new RoleData(1L);

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


    private static boolean test2() {
        Role2Data role2Data = new Role2Data(2L);

        Map<Integer, Integer> map = role2Data.getMap();
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }

        map.keySet().remove(1);
        map.values().remove(2);
        map.put(11, 11);
        map.keySet().removeAll(Arrays.asList(3, 4));
        Iterator<Integer> mapIterator = map.keySet().iterator();
        while (mapIterator.hasNext()) {
            Integer next = mapIterator.next();
            if (next > 5 && next < 8) {
                mapIterator.remove();
            }
        }

        List<String> list = role2Data.getList();
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        for (String s : list) {
//            list.remove(s);
        }
        list.removeAll(Arrays.asList("1", "2"));
        Iterator<String> listIterator = list.iterator();
        while (listIterator.hasNext()) {
            String next = listIterator.next();
            if (next.equals("6")) {
                listIterator.remove();
            }
        }

        System.err.println("list.indexOf:" + list.indexOf("8"));

        Set<Boolean> set = role2Data.getSet();
        set.add(true);
        set.add(false);
        set.removeAll(Arrays.asList(true));

        ItemEntity itemEntity = new ItemEntity().setId(1).setName("111");
        role2Data.getItems().put(itemEntity.getId(), itemEntity);
        role2Data.getItems().remove(itemEntity.getId());
//        roleData2.getItems().keySet().remove(itemEntity.getId());

        System.err.println("roleData2:" + role2Data.toString());
        return true;
    }
}
