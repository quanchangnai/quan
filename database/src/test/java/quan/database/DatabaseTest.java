package quan.database;

import quan.database.role.RoleData;
import quan.database.store.BerkeleyDB;

import java.util.Arrays;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DatabaseTest {

    private static Database database = null;

    public static void main(String[] args) {

        database = new BerkeleyDB(".temp/bdb");

        database.registerCaches(Arrays.asList(RoleData.cache));
        database.open();

        Transaction.execute(DatabaseTest::test1);

        test2();

//        test3();

        database.close();
    }

    private static void test1() {
        RoleData roleData = RoleData.cache.get(0L);
        roleData.setId(1);
        roleData.setName("name" + System.currentTimeMillis());
        roleData.getList().add("aaa");
        database.put(roleData);
    }

    private static void test2() {
        RoleData roleData = RoleData.cache.get(1L);
        System.err.println(roleData);
    }

    private static void test3() {
        database.remove(RoleData.cache.getName(), 1L);
    }


}
