package quan.database;

import java.lang.annotation.*;

/**
 * 声明式事务注解
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}
