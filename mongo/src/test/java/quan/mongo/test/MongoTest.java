package quan.mongo.test;

import quan.mongo.Transaction;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class MongoTest {

    public static void main(String[] args) throws Exception {
        new Role().toString();
        Transaction.enable();

        while (true) {
            test();
            Thread.sleep(5000);
            System.err.println("Thread.sleep(5000)");
        }
    }

    private static void test() {
        try {
            RoleData roleData = new RoleData();
            roleData.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runnable runnable1 = () -> {
            System.err.println("runnable1");
            Runnable runnable2 = () -> {
                System.err.println("runnable2");
            };
            runnable2.run();
        };
        runnable1.run();

    }

}
