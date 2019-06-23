package quan.database;

/**
 * Created by quanchangnai on 2019/6/22.
 */
public class TransactionTest {

    private static RoleData roleData = new RoleData();

    public static void main(String[] args) {
        Transaction.execute(TransactionTest::update1);

        try {
            Transaction.execute(TransactionTest::update2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println(roleData);
    }

    private static void update1() {
        roleData.setId(111);
        roleData.setName("aaa");
    }

    private static void update2() {
        roleData.setId(222);
        roleData.getItems().put(111, 222);
        Transaction.breakdown();
    }
}
