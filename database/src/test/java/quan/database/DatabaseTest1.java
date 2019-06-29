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
public class DatabaseTest1 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest1.class);

    private static Database database = null;

    private static long c = 0;

    public static void main(String[] args) throws Exception {

        database = new BerkeleyDB(".temp/bdb");

        Transaction.setConflictNumThreshold(1);

        for (int i = 0; i < 20; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        Transaction.execute(DatabaseTest1::test1);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            Thread.sleep(500);
        }

        Thread.sleep(2000);

        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Transaction.execute(DatabaseTest1::test2);
                    }

                }
            }.start();
        }

        Thread.sleep(2000);

        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Transaction.execute(DatabaseTest1::test3);
                    }

                }
            }.start();
        }


//        database.close();
    }

    private static Random random = new SecureRandom();

    private static long randomLong() {
        return random.nextInt(100);
    }

    private static void test1() {
        long startTime = System.currentTimeMillis();

        long roleId = randomLong();
        RoleData roleData = RoleData.get(roleId);
        if (roleData == null) {
            roleData = new RoleData();
            roleData.setId(roleId);
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
//            logger.debug("事务:{},单次执行test1()耗时:{},roleData.getList():{}", Transaction.current().getId(), costTime, roleData.getList().size());
//        }
    }

    private static void test2() {
        long roleId = randomLong();
        RoleData roleData = RoleData.get(roleId);
        if (roleData != null) {
            roleData.setName("test2-" + System.currentTimeMillis());
        }
    }

    private static void test3() {
        long roleId = randomLong();
        RoleData.delete(roleId);
    }
}
