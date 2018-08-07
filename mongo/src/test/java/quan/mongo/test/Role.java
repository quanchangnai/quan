package quan.mongo.test;

import quan.mongo.ListWrapper;
import quan.mongo.Transaction;
import quan.mongo.Transactional;

/**
 * Created by quanchangnai on 2018/8/6.
 */
@Transactional
public class Role implements IRole {

    private RoleData roleData = new RoleData();

    public Role() {
        init();
    }

    @Override
    public String toString() {
        return "Role{}";
    }

    @Transactional
    private void init() {
        for (int i = 1; i <= 2; i++) {
            roleData.getItems().add(new ItemData(i, i));
        }
    }

    @Transactional
    @Override
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

        ListWrapper<ItemData> items = (ListWrapper<ItemData>) roleData.getItems();
        System.err.println("items:" + items.size() + "," + items.toDebugString());
        items.get(0).setItemNum(1);

        try {
            update2();
        } catch (Throwable e) {
            System.err.println("e:"+e);
        }

//        Transaction.fail();

    }

    @Transactional
    public void update2() {
        System.err.println("update2=================");
        roleData.getItems().get(1).setItemNum(2);
//        throw new RuntimeException("update exception");

    }
}
