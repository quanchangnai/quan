package quan.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记{@link Service}的方法可被远程调用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Endpoint {
}
