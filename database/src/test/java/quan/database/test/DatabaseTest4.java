package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.Database;
import quan.database.Executor;
import quan.database.MysqlDB;
import quan.database.Transactions;

/**
 * 声明式事务测试
 * Created by quanchangnai on 2019/7/2.
 */
public class DatabaseTest4 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest4.class);

    static Database database;

    static {
//        database = new BerkeleyDB(".temp/bdb");
//        database = new MongoDB(new MongoDB.Config().setClientUri("mongodb://127.0.0.1:27017").setDatabaseName("mdb").setConnectionsNum(1));
        database = new MysqlDB(new MysqlDB.Config());
    }

    static Executor executor = new Executor();

    public static void main(String[] args) throws Exception {
        System.err.println("======================================================");
        test1();
        System.err.println("======================================================");

//        test2();
//        System.err.println("=================="====================================);


//        database.close();
    }


    private static void test1() throws Exception {
        Transactions.setExecutor(executor);
        Role role = Transactions.subclass(Role.class);
        logger.error("role.getClass:{}", role.getClass());

        Transactions.setExecutor(executor);

        Transactions.transform("quan.database.test.Role2");

        Role2 role2 = new Role2();

//        role.login1();
//        role.login2("123456");
        role.login3();
//        role.login4();

//        role2.login();

        Thread.sleep(1000);

        logger.error("test1{}", role);
    }


    private static void test2() throws Exception {
        Transactions.transform("quan.database.test.Role");

        Role role = new Role();
        role.login1();

//        role.login1("123456");

        role.login4();

        Thread.sleep(1000);

        logger.error("test2:{}", role);
    }


}
