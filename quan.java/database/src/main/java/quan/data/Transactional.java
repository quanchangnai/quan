package quan.data;

import quan.common.AspectUtils;

import java.lang.annotation.*;

/**
 * 声明式事务注解，需要先使用{@link AspectUtils#enable()}启用AOP
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}
