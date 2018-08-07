package quan.mongo.test;

import quan.mongo.Transaction;
import quan.mongo.Transactional;

/**
 * Created by quanchangnai on 2018/8/6.
 */
@Transactional
public class Role implements IRole {

    private RoleData roleData = new RoleData();

    @Override
    public String toString() {
        return "Role{}";
    }

    @Override
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

        int size = roleData.getItems().size();
        System.err.println("list:" + size + "," + roleData.getItems());
        roleData.getItems().add(new ItemData(++size, size));

//        Transaction.fail();
        throw new RuntimeException("update exception");
    }
}
