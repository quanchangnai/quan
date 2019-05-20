package quan.transaction.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.transaction.Transaction;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class Test {

    private static final Logger logger = LogManager.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        Transaction.enable();
        test1();
//        test2();
//
//        test3();

    }


    private static void test1() {
        logger.error("测试3个线程开启事务同时修改数据");

        Role role = new Role();

        for (int i = 0; i < 3; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
//                        Transaction.execute(role::test1);
                        role.test1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.error(role.getRoleData());
    }

    private static void test2() {
        Role role = new Role();
        role.test1();
        logger.error(role.getRoleData());
        role.test2();
        logger.error(role.getRoleData());
    }


    private static void test3() {
        PMap<String, String> map = Empty.map();
        map = map.plus("aaa", "bbb");
        map.keySet().remove("aaa");
    }

}
