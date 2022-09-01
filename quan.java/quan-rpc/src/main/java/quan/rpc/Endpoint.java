package quan.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记{@link Service}的方法可被远程调用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Endpoint {

    /**
     * 标记所有参数都是安全的<br/>
     * 原生类型及其包装类型等不可变类型一定是安全的
     */
    boolean paramSafe() default false;

    /**
     * 标记返回结果是安全的
     */
    boolean resultSafe() default true;

}
