package quan.mongo.test;

import quan.mongo.Transactional;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class RoleData {

    @Transactional
    public void say() {
        System.err.println("============say=================");

//        Runnable runnable1=()->{
//            System.err.println("runnable1");
//            Runnable runnable2=()->{
//                System.err.println("runnable2");
//            };
//            runnable2.run();
//        };
//        runnable1.run();

        throw new RuntimeException("say exception");
    }

}
