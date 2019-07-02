package quan.database;

/**
 * 声明式事务测试
 * Created by quanchangnai on 2019/7/2.
 */
public class DatabaseTest4 {

    static Database database = new BerkeleyDB(".temp/bdb");

    static Executor executor = new Executor();

    public static void main(String[] args) throws Exception {
//        test1(executor);
//        System.err.println("==================");

        test1(null);
        System.err.println("==================");

//        test2(executor);
//        System.err.println("==================");

//        test2(null);
//        System.err.println("==================");
    }

    private static void test1(Executor executor) throws Exception {
        Transactions.transform(executor);

        Role role = new Role();
        role.login();

        Thread.sleep(1000);

        System.err.println("test1:" + role.toString());
    }

    private static void test2(Executor executor) throws Exception {
        Role role = Transactions.subclass(Role.class, executor);
        System.err.println("role.getClass:" + role.getClass());

        role.login();

        Thread.sleep(1000);

        System.err.println("test2:" + role.toString());
    }

}
