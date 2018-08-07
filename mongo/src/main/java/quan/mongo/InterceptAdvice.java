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
        if (thrown != null) {
            Transaction.fail();
        }
        Transaction.end();
    }

}
