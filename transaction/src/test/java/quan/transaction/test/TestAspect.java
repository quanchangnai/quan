//package quan.transaction.test;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import quan.transaction.Transaction;
//
///**
// * Created by quanchangnai on 2019/5/19.
// */
//@Aspect
//public class TestAspect {
//
//    @Around("@annotation(quan.transaction.Transactional)")
//    public void around(ProceedingJoinPoint joinPoint) {
//
//        System.err.println("TestAspect.around start");
//        Transaction.execute(() -> {
//            try {
//                joinPoint.proceed();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });
//        System.err.println("TestAspect.around end");
//
//    }
//}
