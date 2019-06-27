package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.role.RoleData;
import quan.database.store.BerkeleyDB;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DatabaseTest1 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest1.class);

    private static Database database = null;

    public static void main(String[] args) throws Exception {

        database = new BerkeleyDB(".temp/bdb");

        for (int i = 0; i < 100; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        Transaction.execute(DatabaseTest1::test1);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            Thread.sleep(1000);
        }

        Thread.sleep(5000);

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Transaction.execute(DatabaseTest1::test2);
                }

            }
        }.start();

//        database.close();
    }

    private static void test1() {
        RoleData roleData = RoleData.get(1L);
        if (roleData == null) {
            roleData = new RoleData();
            roleData.setId(1);
            RoleData.insert(roleData);
        }
        roleData.setName("aaa" + System.currentTimeMillis());
        if (roleData.getList().size() > 10 && roleData.getList().size() / 2 == 0) {
            roleData.getList().remove(0);
        } else {
            roleData.getList().add("aaa");
        }

//        logger.debug("事务:{},roleData:{}", Transaction.current().getId(), roleData);
    }

    private static void test2() {
        RoleData.delete(1L);
    }

}
