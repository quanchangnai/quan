package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.Database;
import quan.database.MongoDB;
import quan.database.Transaction;
import quan.database.Transactions;
import quan.database.role.RoleData;

/**
 * Created by quanchangnai on 2019/6/24.
 */
public class DatabaseTest3 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest3.class);

    private static Database database = null;


    public static void main(String[] args) throws Exception {

//        database = new BerkeleyDB(".temp/bdb");
        database = new MongoDB(new MongoDB.Config().setConnectionString("mongodb://127.0.0.1:27017/test"));

        Transactions.setPrintCountInterval(10);
        Transactions.setConflictThreshold(1);

        //最终结果应该是加200
        for (int i = 0; i < 100; i++) {
            new Thread(() -> Transaction.execute(DatabaseTest3::test1)).start();
        }

//        database.close();
    }


    private static boolean test1() {

        RoleData roleData = RoleData.getOrInsert(1L);
        roleData.setI(roleData.getI() + 1);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        roleData.setI(roleData.getI() + 1);


        return true;
    }

}
