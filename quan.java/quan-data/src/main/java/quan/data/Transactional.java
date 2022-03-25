package quan.data;

import quan.util.AopUtils;

import java.lang.annotation.*;

/**
 * 声明式事务注解，需要先调用{@link AopUtils#enable()} ()}初始化AOP
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

    /**
     * 如果在事务中再开启事务是开启内嵌事务还是直接使用当前事务<br/>
     * 内嵌事务可以独立回滚，外层事务回滚会导致内嵌事务也回滚
     */
    boolean nested() default false;

}
