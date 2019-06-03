package quan.transaction.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import quan.transaction.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by quanchangnai on 2019/5/19.
 */
@Aspect
public class TestAspect {

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    @Around("@annotation(quan.transaction.test.TestAnnotation) ")
    public void around(ProceedingJoinPoint joinPoint) {


        executor.submit(() -> {
            try {
                System.err.println("TestAspect.around start:"+joinPoint.toLongString());
                joinPoint.proceed();
                System.err.println("TestAspect.around end");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });



    }
}
