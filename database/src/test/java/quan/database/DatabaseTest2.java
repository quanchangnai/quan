package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.role.RoleData;
import quan.database.store.BerkeleyDB;

import java.util.Random;

/**
 * Created by quanchangnai on 2019/6/27.
 */
public class DatabaseTest2 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest1.class);

    public static void main(String[] args) throws Exception {
        Database database = new BerkeleyDB(".temp/bdb", 5, 30, 5);

        while (true) {
            Transaction.execute(DatabaseTest2::test1);
            Thread.sleep(20000);
        }

    }

    private static Random random = new Random();
    private static long roleId = 1;

    private static void test1() {
        RoleData roleData = RoleData.get(roleId);
        if (roleData == null) {
            roleData = new RoleData();
            roleData.setId(roleId);
            RoleData.insert(roleData);
        }

        if (roleId < 100) {
            roleId++;
        }

        long i = random.nextInt((int) roleId);

        System.err.println("i:" + i);

        roleData = RoleData.get(i);
        if (roleData != null) {
            logger.error("roleId:{},touchTime:{}", roleData.getId(), roleData.getTouchTime());
        }

    }
}
