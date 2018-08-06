package quan.mongo.test;

import net.bytebuddy.agent.ByteBuddyAgent;
import quan.mongo.Transaction;

import javax.management.relation.Role;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class MongoTest {

    public static void main(String[] args) throws Exception {
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
            roleData.say();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Runnable runnable1 = () -> {
//            System.err.println("runnable1");
//        };
//        runnable1.run();

    }

}
