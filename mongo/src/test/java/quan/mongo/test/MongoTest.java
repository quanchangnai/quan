package quan.mongo.test;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import quan.mongo.Transaction;

import java.lang.instrument.Instrumentation;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class MongoTest {

    public static void main(String[] args) throws Exception {
        Instrumentation instrumentation = ByteBuddyAgent.install();
//        new AgentBuilder.Default()
//                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
//                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
//                .installOn(instrumentation);

        Transaction.enable();

        while (true) {
            test();
            Thread.sleep(5000);
            System.err.println(" Thread.sleep(5000)");
        }
    }

    private static void test() {
//        try {
//            RoleData roleData = new RoleData();
//            roleData.say();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//
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
