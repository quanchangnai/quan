package quan.data;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现声明式事务的切面
 * Created by quanchangnai on 2020/4/28.
 */
@Aspect
public class TransactionAspect {

    @Around("@annotation(quan.data.Transactional) && !staticinitialization(*)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Throwable> exception = new AtomicReference<>();
        Object result = Transaction.execute(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                exception.set(e);
                return null;
            }
        });

        if (exception.get() != null) {
            throw exception.get();
        } else {
            return result;
        }
    }

}
