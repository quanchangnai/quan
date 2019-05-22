package quan.transaction.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcollections.Empty;
import org.pcollections.PMap;
import quan.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class Test {

    private static final Logger logger = LogManager.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        Transaction.enable();
        Role role = new Role();
        test1(role);
        test2(role);

//        test3();


        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add(1, "ccc");
        System.err.println(list);
        list.remove("aaa");
        System.err.println(list);
    }


    private static void test1(Role role) {
        logger.error("测试3个线程开启事务同时修改数据");


        for (int i = 0; i < 100; i++) {
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

    private static void test2(Role role) {
        logger.error(role.getRoleData());
        try {
            role.test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error(role.getRoleData());
    }


    private static void test3() {
        PMap<String, String> map = Empty.map();
        map = map.plus("aaa", "bbb");
        map.keySet().remove("aaa");
    }

}
