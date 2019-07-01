package quan.database.test;

import quan.database.Transaction;

/**
 * Created by quanchangnai on 2019/7/1.
 */
public class Test {

    public static void main(String[] args) {
        Transaction transaction = Transaction.current();
    }

}
