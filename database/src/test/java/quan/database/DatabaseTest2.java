package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.role.RoleData;

import java.util.Random;

/**
 * Created by quanchangnai on 2019/6/27.
 */
public class DatabaseTest2 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest1.class);

    public static void main(String[] args) throws Exception {
        Database database = new BerkeleyDB(".temp/bdb", 5, 30, 5, 0);
        while (true) {
            Transaction.execute(DatabaseTest2::test1);
            Thread.sleep(20000);
        }
//        database.close();
    }

    private static Random random = new Random();
    private static long roleId = 1;

    private static boolean test1() {

        System.err.println("roleId:" + roleId);

        RoleData roleData = RoleData.get(roleId);
        System.err.println("RoleData.get(" + roleId + "):" + roleData);

        if (roleData == null) {
            roleData = new RoleData(roleId);
            RoleData.insert(roleData);
            System.err.println("RoleData.insert():" + roleData);
        }

        long rand1 = random.nextInt((int) roleId) + 1;
        System.err.println("rand1:" + rand1);

        roleData = RoleData.get(rand1);
        if (roleData != null) {
            roleData.setName("name-" + System.currentTimeMillis());
        }
        System.err.println("RoleData.get(" + rand1 + "):" + roleData);

        long rand2 = random.nextInt((int) roleId) + 1;
        System.err.println("rand2:" + rand2);

        RoleData.delete(rand2);
        System.err.println("RoleData.delete(" + rand2 + ")");

        if (roleId < 20) {
            roleId++;
        }

        return true;

    }
}
