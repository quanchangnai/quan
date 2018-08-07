package quan.mongo.test;

import quan.mongo.Transaction;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class MongoTest {

    public static void main(String[] args) throws Exception {
        Transaction.enable();

        Role role = new Role();
        while (true) {
            test(role);
            Thread.sleep(5000);
            System.err.println();
        }
    }

    private static void test(Role role) {
        try {
            role.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Runnable runnable1 = () -> {
//            System.err.println("runnable1");
//            Runnable runnable2 = () -> {
//                System.err.println("runnable2");
//            };
//            runnable2.run();
//        };
//        runnable1.run();

    }

}
