package quan.transaction;

import java.lang.annotation.*;

/**
 * 方法所属的类和方法上都有此注解时，该方法将会在事务中执行
 * Created by quanchangnai on 2019/5/16.
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}
