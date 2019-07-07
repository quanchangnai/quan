package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.BerkeleyDB;
import quan.database.Database;
import quan.database.Transaction;
import quan.database.role.RoleData;

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

        for (int i = 0; i < 10; i++) {
            Transaction.execute(DatabaseTest3::test1);
        }

        database.close();
    }

    private static Random random = new SecureRandom();

    private static boolean test1() {
        long startTime = System.currentTimeMillis();

        long roleId = random.nextInt(3);
//        roleId = 1L;
        RoleData roleData = RoleData.getOrInsert(roleId);
        System.err.println("RoleData.get(roleId):" + roleData);


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
        logger.debug("事务:{},单次执行test1()耗时:{},roleData.getList():{}", Transaction.get().getId(), costTime, roleData.getList().size());
//        }

        Transaction.addAfterTask(() -> System.err.println("事务已提交"), true);
        Transaction.addAfterTask(() -> System.err.println("事务已回滚"), false);

        return true;
    }

}
