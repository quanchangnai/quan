package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 声明式事务测试
 * Created by quanchangnai on 2019/7/2.
 */
public class DatabaseTest4 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest4.class);

    static Database database;

    static {
//        database = new BerkeleyDB(".temp/bdb");
        database = new MongoDB(new MongoDB.Config().setClientUri("mongodb://127.0.0.1:27017").setDatabaseName("mdb"));
    }

    static Executor executor = new Executor();

    public static void main(String[] args) throws Exception {
        System.err.println("======================================================");
//        test1(executor);
//        System.err.println("======================================================");

        test1(null);
        System.err.println("======================================================");

//        test2(executor);
//        System.err.println("=================="====================================);

//        test2(null);
//        System.err.println("======================================================");
    }


    private static void test1(Executor executor) throws Exception {
        Role role = Transactions.subclass(Role.class, executor);
        logger.error("role.getClass:{}", role.getClass());

        Object result = role.login();
        System.err.println("role.login():" + result);

        result = role.login("123456");
        System.err.println("role.login(123456):" + result);

        Thread.sleep(1000);

        logger.error("test1{}", role);
    }


    private static void test2(Executor executor) throws Exception {
        Transactions.transform("quan.database.Role", executor);

        Role role = new Role();
        Object result = role.login();
        System.err.println("role.login():" + result);

        result = role.login("123456");
        System.err.println("role.login(123456):" + result);

//        Transaction.execute(role::login);

        Thread.sleep(1000);

        logger.error("test2:{}", role);
    }


}
