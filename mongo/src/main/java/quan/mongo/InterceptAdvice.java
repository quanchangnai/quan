package quan.mongo;


import net.bytebuddy.asm.Advice;

/**
 * 拦截通知
 * Created by quanchangnai on 2018/5/22.
 */
public class InterceptAdvice {

    @Advice.OnMethodEnter
    public static void onMethodEnter() {
        Transaction.start();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onMethodExit(@Advice.Thrown Throwable thrown) {
        Transaction current = Transaction.current();
        int enterCount = current.subEnterCount();

        boolean failed = current.isFailed();
        if (thrown != null) {
            failed = true;
            Transaction.fail();
        }
        if (enterCount <= 0) {
            if (failed) {
                current.rollback();
            } else {
                current.commit();
            }
        }
    }

}
