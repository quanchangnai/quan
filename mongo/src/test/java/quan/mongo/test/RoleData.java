package quan.mongo.test;

import quan.mongo.Transactional;

/**
 * Created by quanchangnai on 2018/8/6.
 */
@Transactional
public class RoleData extends BaseData {

    @Transactional
    public void update() {
        System.err.println("update=================");

//        Runnable runnable1=()->{
//            System.err.println("runnable1");
//            Runnable runnable2=()->{
//                System.err.println("runnable2");
//            };
//            runnable2.run();
//        };
//        runnable1.run();

//        throw new RuntimeException("update exception");
    }

}
