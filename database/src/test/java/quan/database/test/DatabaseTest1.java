package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.BerkeleyDB;
import quan.database.Database;
import quan.database.Transaction;
import quan.database.Transactions;
import quan.database.role.RoleData;

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

        Transactions.setConflictThreshold(1);

        for (int i = 0; i < 200; i++) {
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

        for (int i = 0; i < 100; i++) {
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

        for (int i = 0; i < 100; i++) {
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

    private static boolean test1() {
        long startTime = System.currentTimeMillis();

        long roleId = random.nextInt(10);
        RoleData roleData = RoleData.get(roleId);
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
//            logger.debug("事务:{},单次执行test1()耗时:{},roleData.getList():{}", Transaction.get().getId(), costTime, roleData.getList().size());
//        }

        return true;
    }

    private static boolean test2() {
        long roleId = random.nextInt(10);
        RoleData roleData = RoleData.get(roleId);
        if (roleData != null) {
            roleData.setName("test2-" + System.currentTimeMillis());
        }

        return true;
    }

    private static boolean test3() {
        long roleId = random.nextInt(10);
        RoleData.delete(roleId);

        return true;
    }
}
