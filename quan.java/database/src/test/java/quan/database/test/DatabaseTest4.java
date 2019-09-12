package quan.database.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.database.BerkeleyDB;
import quan.database.Database;
import quan.database.Executor;
import quan.database.Transactions;

/**
 * 声明式事务测试
 * Created by quanchangnai on 2019/7/2.
 */
public class DatabaseTest4 {

    private static Logger logger = LoggerFactory.getLogger(DatabaseTest4.class);

    static Database database;

    static {
        database = new BerkeleyDB(".temp/bdb" );
//        database = new MongoDB(new MongoDB.Config().setConnectionString("mongodb://127.0.0.1:27017/test"));
//        database = new MySqlDB(new MySqlDB.Config().setConnectionString("jdbc:mysql://localhost:3306/test?user=root&password=123456&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true" ));
    }

    static Executor executor = new Executor();

    public static void main(String[] args) throws Exception {
        System.err.println("======================================================" );
        test1();
        System.err.println("======================================================" );

//        test2();
//        System.err.println("=================="====================================);


//        database.close();
    }


    private static void test1() throws Exception {
//        Class<? extends Role> subclass = Transactions.subclass(Role.class);
//        Role role = subclass.getDeclaredConstructor().newInstance();

        Role role = Transactions.proxy(Role.class, executor);
        logger.error("role.getClass:{}", role.getClass());


        role.login1();
//        role.login2("123456");
//        role.login3();
//        role.login4();
        role.update();


//        Transactions.transform("quan.database.test.Role2");
//        Role2 role2 = new Role2();
//        role2.login();

        Thread.sleep(1000);

        logger.error("test1{}", role);
    }


    private static void test2() throws Exception {
        Transactions.transform("quan.database.test.Role" );

        Role role = new Role();
        role.login1();

//        role.login1("123456");

        role.login4();

        Thread.sleep(1000);

        logger.error("test2:{}", role);
    }


}
