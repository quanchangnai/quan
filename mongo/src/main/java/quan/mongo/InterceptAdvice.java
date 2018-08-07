package quan.mongo;


import net.bytebuddy.asm.Advice;

/**
 * 拦截通知
 * Created by quanchangnai on 2018/8/6.
 */
public class InterceptAdvice {

    @Advice.OnMethodEnter
    public static void onMethodEnter() {
        Transaction.start();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onMethodExit(@Advice.Thrown Throwable thrown) {
        Transaction.end(thrown != null);
    }

}
