package quan.mongotest;

import quan.mongo.Transaction;
import quan.mongo.Transactional;
import quan.mongotest.data.ItemData;
import quan.mongotest.data.RoleData;

/**
 * Created by quanchangnai on 2018/8/6.
 */
@Transactional
public class Role {

    private RoleData roleData = new RoleData();

    public Role() {
        Transaction.execute(this::init);
    }

    @Override
    public String toString() {
        return "Role{}";
    }

    private void init() {
        for (int i = 1; i <= 2; i++) {
            roleData.getItems().add(new ItemData(i, i));
        }
    }

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

        System.err.println("roleData1:" + roleData.toDebugString());
        if (roleData.getItems().isEmpty()) {
            roleData.getItems().add(new ItemData(1, 2));
        }
        roleData.getItems().get(0).setItemNum(2);
//        roleData.setItem(new ItemData(100, 100));
        if (roleData.getItem() != null) {
            roleData.getItem().setItemNum(200);
            roleData.setItem(null);
        }

        try {
            update2();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Transaction.fail();
        System.err.println("roleData2:" + roleData.toDebugString());
    }

    @Transactional
    public void update2() {
        System.err.println("update2=================");
//        roleData.getItems().get(1).setItemNum(12);
        if (roleData.getItems().size() > 1) {
            ItemData itemData = roleData.getItems().remove(1);
            System.err.println("itemData=" + itemData.toDebugString());
        }
        throw new RuntimeException("update exception");

    }
}
