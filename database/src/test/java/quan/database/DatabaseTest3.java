package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.role.RoleData;
import quan.database.store.BerkeleyDB;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DatabaseTest3 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest3.class);

    private static Database database = null;

    private static long c = 0;

    public static void main(String[] args) throws Exception {

        database = new BerkeleyDB(".temp/bdb");

        Transaction.execute(DatabaseTest3::test1);

        database.close();
    }

    private static Random random = new SecureRandom();

    private static void test1() {
        long startTime = System.currentTimeMillis();

        long roleId = random.nextInt(10);
        roleId = 1L;
        RoleData roleData = RoleData.get(roleId);
        System.err.println("RoleData.get(roleId):" + roleData);

        if (roleData == null) {
            roleData = new RoleData(roleId);
            RoleData.insert(roleData);
        }

        int s = 0;
        for (int i = 0; i < 20; i++) {
            roleData.setName("test1-" + System.currentTimeMillis());
            if (roleData.getList().size() > 200) {
                roleData.getList().clear();
            }
            roleData.getList().add("aaa" + i);

            Set<String> set = new HashSet<>();
            set.addAll(roleData.getList());
            set.add(String.valueOf(System.currentTimeMillis()));
            s += set.size();
        }

        c += s;

        long costTime = System.currentTimeMillis() - startTime;
//        if (costTime > 2) {
        logger.debug("事务:{},单次执行test1()耗时:{},roleData.getList():{}", Transaction.current().getId(), costTime, roleData.getList().size());
//        }
    }

}