package quan.data;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现声明式事务的切面<br/>
 * Created by quanchangnai on 2020/4/28.
 */
@Aspect
public class TransactionAspect {

    @Around("@annotation(quan.data.Transactional) && execution(* *(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        boolean nested = method.getAnnotation(Transactional.class).nested();
        AtomicReference<Throwable> exceptionReference = new AtomicReference<>();

        Object result = Transaction.execute(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                Transaction.rollback();
                exceptionReference.set(e);
                return null;
            }
        }, nested);

        Throwable exception = exceptionReference.get();
        if (exception != null) {
            throw exception;
        } else {
            return result;
        }
    }

}
