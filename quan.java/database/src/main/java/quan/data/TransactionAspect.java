package quan.data;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 实现声明式事务的切面<br/>
 * 加载时间必须先于被切类，否则会导致环绕通知不能内联<br/>
 * Created by quanchangnai on 2020/4/28.
 */
@Aspect
public class TransactionAspect {

    @Pointcut("@annotation(quan.data.Transactional) && execution(* *(..))")
    private void pointcut() {
    }

    //环绕通知内联支持不是太好
    //@Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        boolean nested = method.getAnnotation(Transactional.class).nested();

        Transaction transaction = Transaction.begin(nested);
        try {
            //不能在around方法外面调用joinPoint，否则会导致不能内联
            return joinPoint.proceed();
        } catch (Throwable e) {
            transaction.failed = true;
            throw e;
        } finally {
            Transaction.end(transaction);
        }
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        boolean nested = method.getAnnotation(Transactional.class).nested();
        Transaction.begin(nested);
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            transaction.failed = true;
        }
    }

    @After("pointcut()")
    public void afterFinally() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            Transaction.end(transaction);
        }
    }

}
