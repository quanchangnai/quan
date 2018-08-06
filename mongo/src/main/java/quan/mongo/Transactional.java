package quan.mongo;

import java.lang.annotation.*;

/**
 * 被注解的方法将会在事务中执行
 * Created by quanchangnai on 2018/8/6.
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}
